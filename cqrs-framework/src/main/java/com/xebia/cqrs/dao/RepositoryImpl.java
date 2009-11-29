package com.xebia.cqrs.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.xebia.cqrs.bus.Bus;
import com.xebia.cqrs.bus.BusSynchronization;
import com.xebia.cqrs.domain.AggregateRoot;
import com.xebia.cqrs.domain.AggregateRootNotFoundException;
import com.xebia.cqrs.domain.Event;
import com.xebia.cqrs.domain.Repository;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.cqrs.eventstore.EventStore;

@org.springframework.stereotype.Repository
public class RepositoryImpl implements Repository, BusSynchronization {

    @Autowired
    private EventStore<Event> eventStore;
    
    @Autowired
    private Bus bus;

    private final ThreadLocal<Session> sessions = new ThreadLocal<Session>() {
        @Override
        protected Session initialValue() {
            return new Session();
        }
    };
    
    public RepositoryImpl() {
    }
    
    public RepositoryImpl(EventStore<Event> eventStore, Bus bus) {
        this.eventStore = eventStore;
        this.bus = bus;
    }

    public <T extends AggregateRoot> T get(Class<T> type, VersionedId id) {
        return sessions.get().get(type, id);
    }

    public <T extends AggregateRoot> void add(T aggregate) {
        if (aggregate != null) {
            sessions.get().add(aggregate);
        }
    }

    public void afterHandleMessage() {
        sessions.get().afterHandleMessage();
    }

    public void beforeHandleMessage() {
        sessions.get().beforeHandleMessage();
    }
    
    private class Session {

        private Map<UUID, AggregateRoot> aggregatesById = new HashMap<UUID, AggregateRoot>();
        private Queue<AggregateRoot> unsavedAggregates = new LinkedList<AggregateRoot>();
        
        public <T extends AggregateRoot> T get(Class<T> type, VersionedId id) {
            T result = type.cast(aggregatesById.get(id.getId()));
            if (result != null) {
                eventStore.verifyVersion(result, id);
            } else {
                result = eventStore.loadEventSource(type, id);
                verifyExistence(type, id, result);
                addToSession(result);
            }
            return result;
        }

        private void verifyExistence(Class<?> type, VersionedId id, Object result) {
            if (result == null) {
                throw new AggregateRootNotFoundException(type.getName(), id.getId());
            }
        }

        public <T extends AggregateRoot> void add(T aggregate) {
            addToSession(aggregate);
        }

        private <T extends AggregateRoot> void addToSession(T aggregate) {
            aggregatesById.put(aggregate.getVersionedId().getId(), aggregate);
            unsavedAggregates.add(aggregate);
        }

        public void beforeHandleMessage() {
        }
        
        public void afterHandleMessage() {
            Collection<Object> notifications = new ArrayList<Object>();
            for (AggregateRoot aggregate : unsavedAggregates) {
                notifications.addAll(aggregate.getNotifications());
                aggregate.clearNotifications();
                
                bus.publish(aggregate.getUnsavedEvents());
                
                eventStore.storeEventSource(aggregate);
            }
            bus.reply(notifications);
            
            aggregatesById.clear();
            unsavedAggregates.clear();
        }

    }

}
