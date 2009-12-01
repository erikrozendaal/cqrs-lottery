package com.xebia.cqrs.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.xebia.cqrs.bus.Bus;
import com.xebia.cqrs.bus.BusSynchronization;
import com.xebia.cqrs.domain.AggregateRoot;
import com.xebia.cqrs.domain.AggregateRootNotFoundException;
import com.xebia.cqrs.domain.Event;
import com.xebia.cqrs.domain.Repository;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.cqrs.eventstore.EventSink;
import com.xebia.cqrs.eventstore.EventSource2;
import com.xebia.cqrs.eventstore.EventStore2;

@org.springframework.stereotype.Repository
public class RepositoryImpl implements Repository, BusSynchronization {

    public static class AggregateRootSource implements EventSource2<Event> {

        private final AggregateRoot aggregateRoot;

        public AggregateRootSource(AggregateRoot aggregateRoot) {
            this.aggregateRoot = aggregateRoot;
        }

        public String getType() {
            return aggregateRoot.getClass().getName();
        }

        public long getVersion() {
            return aggregateRoot.getVersionedId().getVersion();
        }

        public long getTimestamp() {
            return System.currentTimeMillis();
        }

        public List<? extends Event> getEvents() {
            return aggregateRoot.getUnsavedEvents();
        }

    }

    public static class AggregateRootSink<T extends AggregateRoot> implements EventSink<Event> {

        private final Class<T> expectedType;
        private final UUID id;

        private Class<? extends T> actualType;
        private long actualVersion;
        private T aggregateRoot;

        
        public AggregateRootSink(Class<T> expectedType, UUID id) {
            this.expectedType = expectedType;
            this.id = id;
        }

        public void setType(String type) {
            try {
                actualType = Class.forName(type).asSubclass(expectedType);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        public void setVersion(long version) {
            actualVersion = version + 1;
        }

        public void setTimestamp(long timestamp) {
            // TODO Auto-generated method stub

        }

        public void setEvents(Iterable<? extends Event> events) {
            instantiateAggregateRoot();
            aggregateRoot.loadFromHistory(events);
        }

        private void instantiateAggregateRoot() {
            try {
                Constructor<? extends T> constructor = actualType.getConstructor(VersionedId.class);
                aggregateRoot = constructor.newInstance(VersionedId.forSpecificVersion(id, actualVersion));
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        public T getAggrateRoot() {
            return aggregateRoot;
        }

    }

    private final EventStore2<Event> eventStore;
    private final Bus bus;

    private final ThreadLocal<Session> sessions = new ThreadLocal<Session>() {
        @Override
        protected Session initialValue() {
            return new Session();
        }
    };
    
    @Autowired
    public RepositoryImpl(EventStore2<Event> eventStore, Bus bus) {
        this.eventStore = eventStore;
        this.bus = bus;
    }
    
    public <T extends AggregateRoot> T getById(Class<T> type, UUID id) {
        return sessions.get().getById(type, id);
    }

    public <T extends AggregateRoot> T getByVersionedId(Class<T> type, VersionedId id) {
        return sessions.get().getByVersionedId(type, id);
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
        private Queue<AggregateRoot> added = new LinkedList<AggregateRoot>();
        private Queue<AggregateRoot> loaded = new LinkedList<AggregateRoot>();

        public <T extends AggregateRoot> T getById(Class<T> expectedType, UUID id) {
            T result = expectedType.cast(aggregatesById.get(id));
            if (result != null) {
                return result;
            }

            try {
                AggregateRootSink<T> sink = new AggregateRootSink<T>(expectedType, id);
                eventStore.loadEventsFromLatestStreamVersion(id, sink);
                return sink.getAggrateRoot();
            } catch (EmptyResultDataAccessException ex) {
                throw new AggregateRootNotFoundException(expectedType.getName(), id);
            }
        }
        
        public <T extends AggregateRoot> T getByVersionedId(Class<T> expectedType, VersionedId id) {
            T result = expectedType.cast(aggregatesById.get(id.getId()));
            if (result != null) {
                if (!id.nextVersion().equals(result.getVersionedId())) {
                    throw new OptimisticLockingFailureException("actual: " + (result.getVersionedId().getVersion() - 1) + ", expected: " + id.getVersion());
                }
                return result;
            }
            
            try {
                AggregateRootSink<T> sink = new AggregateRootSink<T>(expectedType, id.getId());
                eventStore.loadEventsFromSpecificStreamVersion(id.getId(), id.getVersion(), sink);
                result = sink.getAggrateRoot();
                addToSession(result);
                loaded.add(result);
                return result;
            } catch (EmptyResultDataAccessException ex) {
                throw new AggregateRootNotFoundException(expectedType.getName(), id.getId());
            }
        }

        public <T extends AggregateRoot> void add(T aggregate) {
            if (aggregate.getUnsavedEvents().isEmpty()) {
                throw new IllegalArgumentException("aggregate has no unsaved changes");
            }
            addToSession(aggregate);
            added.add(aggregate);
        }

        private <T extends AggregateRoot> void addToSession(T aggregate) {
            AggregateRoot previous = aggregatesById.put(aggregate.getVersionedId().getId(), aggregate);
            if (previous != null && previous != aggregate) {
                throw new IllegalStateException("multiple instances with same id " + aggregate.getVersionedId().getId());
            }
        }

        public void beforeHandleMessage() {
        }
        
        public void afterHandleMessage() {
            Collection<Object> notifications = new ArrayList<Object>();
            for (AggregateRoot aggregate : added) {
                notifications.addAll(aggregate.getNotifications());
                aggregate.clearNotifications();
                
                bus.publish(aggregate.getUnsavedEvents());
                
                eventStore.createEventStream(aggregate.getVersionedId().getId(), new AggregateRootSource(aggregate));
                aggregate.incrementVersion();
                aggregate.clearUnsavedEvents();
            }
            for (AggregateRoot aggregate : loaded) {
                notifications.addAll(aggregate.getNotifications());
                aggregate.clearNotifications();
                
                bus.publish(aggregate.getUnsavedEvents());
                
                eventStore.storeEventsIntoStream(aggregate.getVersionedId().getId(), aggregate.getVersionedId().getVersion() - 1, new AggregateRootSource(aggregate));
                aggregate.incrementVersion();
                aggregate.clearUnsavedEvents();
            }
            bus.reply(notifications);
            
            added.clear();
            loaded.clear();

            // should be done just before transaction commit...
            aggregatesById.clear();
        }

    }

}
