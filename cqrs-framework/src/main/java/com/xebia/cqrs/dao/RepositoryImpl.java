package com.xebia.cqrs.dao;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.xebia.cqrs.bus.Bus;
import com.xebia.cqrs.domain.AggregateRoot;
import com.xebia.cqrs.domain.AggregateRootNotFoundException;
import com.xebia.cqrs.domain.Event;
import com.xebia.cqrs.domain.Repository;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.cqrs.eventstore.EventStore;

@org.springframework.stereotype.Repository
public class RepositoryImpl implements Repository {

    @Autowired
    private EventStore<Event> eventStore;
    
    @Autowired
    private Bus bus;

    public RepositoryImpl() {
    }
    
    public RepositoryImpl(EventStore<Event> eventStore, Bus bus) {
        this.eventStore = eventStore;
        this.bus = bus;
    }
    
    public <T extends AggregateRoot> T getById(Class<T> type, UUID id) {
        T result = eventStore.loadEventSource(type, id);
        verifyExistence(type, id, result);
        return result;
    }

    public <T extends AggregateRoot> T getByVersionedId(Class<T> type, VersionedId id) {
        T result = eventStore.loadEventSource(type, id);
        verifyExistence(type, id.getId(), result);
        return result;
    }

    private void verifyExistence(Class<?> type, UUID id, Object result) {
        if (result == null) {
            throw new AggregateRootNotFoundException(type.getName(), id);
        }
    }

    public <T extends AggregateRoot> void save(T aggregate) {
        bus.reply(aggregate.getNotifications());
        bus.publish(aggregate.getUnsavedEvents());
        
        eventStore.storeEventSource(aggregate);

        aggregate.clearUnsavedEvents();
        aggregate.clearNotifications();
    }

}
