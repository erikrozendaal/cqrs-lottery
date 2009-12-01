package com.xebia.cqrs.eventstore.inmemory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.xebia.cqrs.eventstore.EventSink;
import com.xebia.cqrs.eventstore.EventSource;
import com.xebia.cqrs.eventstore.EventStore;

/**
 * Stores and tracks ordered streams of events.
 */
public class InMemoryEventStore<E> implements EventStore<E> {
    
    public Map<UUID, EventStream<E>> eventStreams = new HashMap<UUID, EventStream<E>>();
    
    public void createEventStream(UUID streamId, EventSource<E> source) {
        if (eventStreams.containsKey(streamId)) {
            throw new DataIntegrityViolationException("stream already exists " + streamId);
        }
        eventStreams.put(streamId, new EventStream<E>(source.getType(), source.getVersion(), source.getTimestamp(), source.getEvents()));
    }
    
    public void storeEventsIntoStream(UUID streamId, long expectedVersion, EventSource<E> source) {
        EventStream<E> stream = getStream(streamId);
        if (stream.getVersion() != expectedVersion) {
            throw new OptimisticLockingFailureException("stream " + streamId + ", actual version: " + stream.getVersion() + ", expected version: " + expectedVersion);
        }
        stream.setVersion(source.getVersion());
        stream.setTimestamp(source.getTimestamp());
        stream.addEvents(source.getEvents());
    }

    public void loadEventsFromLatestStreamVersion(UUID streamId, EventSink<E> sink) {
        EventStream<E> stream = getStream(streamId);
        sink.setType(stream.getType());
        stream.sendEventsAtVersionToSink(stream.getVersion(), sink);
    }
    
    public void loadEventsFromSpecificStreamVersion(UUID streamId, long expectedVersion, EventSink<E> sink) {
        EventStream<E> stream = getStream(streamId);
        if (stream.getVersion() != expectedVersion) {
            throw new OptimisticLockingFailureException("stream " + streamId + ", actual version: " + stream.getVersion() + ", expected version: " + expectedVersion);
        }
        sink.setType(stream.getType());
        stream.sendEventsAtVersionToSink(stream.getVersion(), sink);
    }
    
    public void loadEventsFromStreamAtVersion(UUID streamId, long version, EventSink<E> sink) {
        EventStream<E> stream = getStream(streamId);
        sink.setType(stream.getType());

        long actualVersion = Math.min(stream.getVersion(), version);
        stream.sendEventsAtVersionToSink(actualVersion, sink);
    }
    
    public void loadEventsFromStreamAtTimestamp(UUID streamId, long timestamp, EventSink<E> sink) {
        EventStream<E> stream = getStream(streamId);
        sink.setType(stream.getType());

        long actualTimestamp = Math.min(stream.getTimestamp(), timestamp);
        stream.sendEventsAtTimestampToSink(actualTimestamp, sink);
    }

    public EventStream<E> getStream(UUID streamId) {
        EventStream<E> stream = eventStreams.get(streamId);
        if (stream == null) {
            throw new EmptyResultDataAccessException("unknown event stream " + streamId, 1);
        }
        return stream;
    }
    
    private static class VersionedEvent<E> {
        private final long version;
        private final long timestamp;
        private final E event;

        public VersionedEvent(long version, long timestamp, E event) {
            this.version = version;
            this.timestamp = timestamp;
            this.event = event;
        }
        
        public long getVersion() {
            return version;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public E getEvent() {
            return event;
        }
    }
    
    public static class EventStream<E> {
        private final String type;
        private long version;
        private long timestamp;
        private final List<VersionedEvent<E>> events = new ArrayList<VersionedEvent<E>>();
        
        public EventStream(String type, long version, long timestamp, Collection<? extends E> initialEvents) {
            this.type = type;
            this.version = version;
            this.timestamp = timestamp;
            addEvents(initialEvents);
        }

        public void sendEventsAtVersionToSink(long version, EventSink<E> sink) {
            List<E> result = new ArrayList<E>();
            VersionedEvent<E> lastEvent = null;
            for (VersionedEvent<E> event : events) {
                if (event.getVersion() > version) {
                    break;
                }
                lastEvent = event;
                result.add(event.getEvent());
            }

            sendEventsToSink(result, lastEvent, sink);
        }

        public void sendEventsAtTimestampToSink(long timestamp, EventSink<E> sink) {
            List<E> result = new ArrayList<E>();
            VersionedEvent<E> lastEvent = null;
            for (VersionedEvent<E> event : events) {
                if (event.getTimestamp() > timestamp) {
                    break;
                }
                lastEvent = event;
                result.add(event.getEvent());
            }
            
            sendEventsToSink(result, lastEvent, sink);
        }

        private void sendEventsToSink(List<E> events, VersionedEvent<E> lastEvent, EventSink<E> sink) {
            if (lastEvent == null) {
                throw new EmptyResultDataAccessException("no event found for specified version or timestamp", 1);
            }
            sink.setVersion(lastEvent.getVersion());
            sink.setTimestamp(lastEvent.getTimestamp());
            sink.setEvents(events);
        }

        public String getType() {
            return type;
        }

        public long getVersion() {
            return version;
        }

        public void setVersion(long version) {
            if (this.version > version) {
                throw new IllegalArgumentException("version cannot decrease");
            }
            this.version = version;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            if (this.timestamp > timestamp) {
                throw new IllegalArgumentException("timestamp cannot decrease");
            }
            this.timestamp = timestamp;
        }

        public void addEvents(Collection<? extends E> eventsToAdd) {
            for (E event : eventsToAdd) {
                this.events.add(new VersionedEvent<E>(this.version, this.timestamp, event));
            }
        }

    }

}
