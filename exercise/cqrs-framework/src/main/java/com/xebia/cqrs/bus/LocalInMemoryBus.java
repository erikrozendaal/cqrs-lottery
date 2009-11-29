package com.xebia.cqrs.bus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.xebia.cqrs.commands.Command;
import com.xebia.cqrs.events.Event;
import com.xebia.cqrs.events.Notification;

@Service
@Transactional
public class LocalInMemoryBus implements Bus {
    
    private static final Logger LOG = Logger.getLogger(LocalInMemoryBus.class);
    
    private final Multimap<Class<?>, Handler<?>> handlers = HashMultimap.create();

    private final ThreadLocal<Queue<Event>> eventQueue = new ThreadLocal<Queue<Event>>() {
        protected java.util.Queue<Event> initialValue() {
            return new LinkedList<Event>();
        }
    };
    private final ThreadLocal<CurrentMessageInformation> state = new ThreadLocal<CurrentMessageInformation>() {
        protected CurrentMessageInformation initialValue() {
            return new CurrentMessageInformation(null);
        }
    };
    
    public Response sendAndWaitForResponse(Command command) {
        try {
            Collection<Notification> replies = dispatchMessage(command);
            dispatchAllQueuedEvents();
            return new Response(replies);
        } catch (Exception ex) {
            throw new MessageHandlingException(ex);
        }
    }

    public void reply(Notification notification) {
        reply(Collections.singleton(notification));
    }
    
    public void reply(Collection<? extends Notification> notifications) {
        if (state.get().currentMessage instanceof Command) {
            state.get().addReplies(notifications);
        } else {
            throw new MessageHandlingException("current message is not a command");
        }
    }

    public void publish(Event event) {
        publish(Collections.singleton(event));
    }

    public void publish(Collection<? extends Event> events) {
        try {
            eventQueue.get().addAll(events);
            if (state.get().currentMessage == null) {
                dispatchAllQueuedEvents();
            }
        } catch (Exception ex) {
            throw new MessageHandlingException(ex);
        }
    }

    public Object getCurrentMessage() {
        return state.get().currentMessage;
    }

    @Autowired
    public void setHandlers(Handler<?>... injectedHandlers) {
        handlers.clear();
        for (Handler<?> handler : injectedHandlers) {
            handlers.put(handler.getMessageType(), handler);
        }
    }
    
    private void dispatchAllQueuedEvents() throws Exception {
        try {
            while (!eventQueue.get().isEmpty()) {
                dispatchMessage(eventQueue.get().poll());
            }
        } catch (Exception e) {
            eventQueue.get().clear();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<Notification> dispatchMessage(Object message) throws Exception {
        Validate.notNull(message, "message is required");
        LOG.debug("dispatching message: " + message);
        CurrentMessageInformation savedState = state.get();
        try {
            state.set(new CurrentMessageInformation(message)); 
            Collection<Handler<?>> matchedHandlers = handlers.get(message.getClass());
            if (matchedHandlers.isEmpty()) {
                throw new IllegalStateException("no matching handlers for message " + message);
            }
            for (Handler handler : matchedHandlers) {
                handler.handleMessage(message);
            }
            return state.get().replies;
        } finally {
            state.set(savedState);
        }
    }

    private static class CurrentMessageInformation {
        public Object currentMessage;
        public Collection<Notification> replies = new ArrayList<Notification>();
        
        public CurrentMessageInformation(Object currentMessage) {
            this.currentMessage = currentMessage;
        }

        void addReplies(Collection<? extends Notification> notifications) {
            replies.addAll(notifications);
        }
    }

}
