package com.xebia.cqrs.bus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

@Service
@Transactional
public class LocalInMemoryBus implements Bus {
    
    private static final Logger LOG = Logger.getLogger(LocalInMemoryBus.class);
    
    private final Multimap<Class<?>, Handler<?>> handlers = HashMultimap.create();

    private final ThreadLocal<Queue<Object>> eventQueue = new ThreadLocal<Queue<Object>>() {
        protected java.util.Queue<Object> initialValue() {
            return new LinkedList<Object>();
        }
    };
    private final ThreadLocal<CurrentMessageInformation> state = new ThreadLocal<CurrentMessageInformation>() {
        protected CurrentMessageInformation initialValue() {
            return new CurrentMessageInformation(null);
        }
    };
    
    public void send(Object message) throws MessageHandlingException {
        dispatchMessage(message);
        dispatchAllQueuedMessages();
    }
    
    public Response sendAndWaitForResponse(Object command) {
        List<Response> responses = dispatchMessage(command);
        if (responses.isEmpty()) {
            throw new MessageHandlingException("no response while executing command " + command);
        }

        dispatchAllQueuedMessages();

        if (LOG.isDebugEnabled() && responses.size() > 1) {
            LOG.debug("additional responses ignored: " + responses.subList(1, responses.size()));
        }
        return responses.get(0);
    }

    public void reply(Object message) {
        reply(Collections.singleton(message));
    }
    
    public void reply(Iterable<?> messages) {
        if (getCurrentMessage() == null) {
            throw new MessageHandlingException("no current message to reply to");
        }

        state.get().addReplies(messages);
        publish(messages);
    }

    public void publish(Object message) {
        publish(Collections.singleton(message));
    }

    public void publish(Iterable<?> messages) {
        Iterables.addAll(eventQueue.get(), messages);
        if (getCurrentMessage() == null) {
            dispatchAllQueuedMessages();
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
    
    private void dispatchAllQueuedMessages() {
        try {
            while (!eventQueue.get().isEmpty()) {
                dispatchMessage(eventQueue.get().poll());
            }
        } catch (RuntimeException e) {
            eventQueue.get().clear();
            throw e;
        }
    }

    private List<Response> dispatchMessage(Object message) {
        Validate.notNull(message, "message is required");
        CurrentMessageInformation savedState = state.get();
        try {
            state.set(new CurrentMessageInformation(message)); 
            invokeHandlers(message);
            return state.get().responses;
        } finally {
            state.set(savedState);
        }
    }

    @SuppressWarnings("unchecked")
    private void invokeHandlers(Object message) {
        try {
            Collection<Handler<?>> matchedHandlers = handlers.get(message.getClass());
            if (LOG.isDebugEnabled()) {
                if (matchedHandlers.isEmpty()) {
                    LOG.debug("no handlers registered for message of " + message.getClass());
                } else {
                    LOG.debug("dispatching to handlers: " + message);
                }
            }
            for (Handler handler : matchedHandlers) {
                handler.handleMessage(message);
            }
        } catch (Exception ex) {
            throw new MessageHandlingException(ex);
        }
    }

    private static class CurrentMessageInformation {
        public Object currentMessage;
        public List<Response> responses = new ArrayList<Response>();
        
        public CurrentMessageInformation(Object currentMessage) {
            this.currentMessage = currentMessage;
        }

        void addReplies(Iterable<?> messages) {
            responses.add(new Response(messages));
        }
    }

}
