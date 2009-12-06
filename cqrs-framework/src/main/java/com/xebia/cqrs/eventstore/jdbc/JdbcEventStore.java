package com.xebia.cqrs.eventstore.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.xebia.cqrs.eventstore.EventSerializer;
import com.xebia.cqrs.eventstore.EventSink;
import com.xebia.cqrs.eventstore.EventSource;
import com.xebia.cqrs.eventstore.EventStore;

public class JdbcEventStore<E> implements EventStore<E> {

    private static final Logger LOG = Logger.getLogger(JdbcEventStore.class);
    
    private final SimpleJdbcTemplate jdbcTemplate;
    
    private final EventSerializer<E> eventSerializer;

    @Autowired
    public JdbcEventStore(SimpleJdbcTemplate jdbcTemplate, EventSerializer<E> eventSerializer) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventSerializer = eventSerializer;
    }
    
    @PostConstruct 
    void init() {
        try {
            jdbcTemplate.update("drop table event if exists");
            jdbcTemplate.update("drop table event_stream if exists");
            jdbcTemplate.update("create table event_stream(id varchar primary key, type varchar not null, version bigint not null, timestamp timestamp not null, next_event_sequence bigint not null)");
            jdbcTemplate.update("create table event(event_stream_id varchar not null, sequence_number bigint not null, version bigint not null, timestamp timestamp not null, data varchar not null, " 
                    + "primary key (event_stream_id, sequence_number), foreign key (event_stream_id) references event_stream (id))");
        } catch (DataAccessException ex) {
            LOG.info("init database exception", ex);
        }
    }

    public void createEventStream(UUID streamId, EventSource<E> source) throws DataIntegrityViolationException {
        long version = source.getVersion();
        long timestamp = source.getTimestamp();
        List<? extends E> events = source.getEvents();
        jdbcTemplate.update("insert into event_stream (id, type, version, timestamp, next_event_sequence) values (?, ?, ?, ?, ?)",
                streamId.toString(), 
                source.getType(),
                version,
                new Date(timestamp),
                events.size());
        saveEvents(streamId, version, timestamp, 0, events);
    }
    
    public void storeEventsIntoStream(UUID streamId, long expectedVersion, EventSource<E> source) {
        long version = source.getVersion();
        long timestamp = source.getTimestamp();
        List<? extends E> events = source.getEvents();
        
        EventStream stream = getEventStream(streamId);
        int count = jdbcTemplate.update("update event_stream set version = ?, timestamp = ?, next_event_sequence = ? where id = ? and version = ?",
                version,
                new Date(timestamp),
                stream.getNextEventSequence() + events.size(),
                streamId.toString(), 
                expectedVersion);
        if (count != 1) {
            throw new OptimisticLockingFailureException("id: " + streamId + "; actual: " + stream.getVersion() + "; expected: " + expectedVersion);
        }
        if (version < stream.getVersion()) {
            throw new IllegalArgumentException("version cannot decrease");
        }
        if (timestamp < stream.getTimestamp()) {
            throw new IllegalArgumentException("timestamp cannot decrease");
        }
        
        saveEvents(streamId, version, timestamp, stream.getNextEventSequence(), events);
    }

    public void loadEventsFromLatestStreamVersion(final UUID streamId, EventSink<E> sink) {
        EventStream stream = getEventStream(streamId);
        List<StoredEvent<E>> storedEvents = loadEventsUptoVersion(stream, stream.getVersion());

        sendEventsToSink(stream, storedEvents, sink);
    }

    public void loadEventsFromExpectedStreamVersion(UUID streamId, long expectedVersion, EventSink<E> sink) {
        EventStream stream = getEventStream(streamId);
        if (stream.getVersion() != expectedVersion) {
            throw new OptimisticLockingFailureException("id: " + streamId + "; actual: " + stream.getVersion() + "; expected: " + expectedVersion);
        }
        List<StoredEvent<E>> storedEvents = loadEventsUptoVersion(stream, stream.getVersion());

        sendEventsToSink(stream, storedEvents, sink);
    }

    public void loadEventsFromStreamUptoVersion(UUID streamId, long version, EventSink<E> sink) {
        EventStream stream = getEventStream(streamId);
        List<StoredEvent<E>> storedEvents = loadEventsUptoVersion(stream, version);

        sendEventsToSink(stream, storedEvents, sink);
    }

    public void loadEventsFromStreamUptoTimestamp(UUID streamId, long timestamp, EventSink<E> sink) {
        EventStream stream = getEventStream(streamId);
        List<StoredEvent<E>> storedEvents = loadEventsUptoTimestamp(stream, timestamp);

        sendEventsToSink(stream, storedEvents, sink);
    }

    private void saveEvents(UUID streamId, long version, long timestamp, int nextEventSequence, List<? extends E> events) {
        for (E event : events) {
            jdbcTemplate.update("insert into event(event_stream_id, sequence_number, version, timestamp, data) values (?, ?, ?, ?, ?)",
                    streamId.toString(), 
                    nextEventSequence++,
                    version,
                    new Date(timestamp),
                    eventSerializer.serialize(event));
        }
    }

    private EventStream getEventStream(final UUID streamId) {
        return jdbcTemplate.queryForObject(
                "select type, version, timestamp, next_event_sequence from event_stream where id = ?", 
                new EventStreamRowMapper(streamId), 
                streamId.toString());
    }

    private List<StoredEvent<E>> loadEventsUptoVersion(EventStream stream, long version) {
        List<StoredEvent<E>> storedEvents = jdbcTemplate.query(
                "select version, timestamp, data from event where event_stream_id = ? and version <= ? order by sequence_number", 
                new StoredEventRowMapper(),
                stream.getId().toString(), version);
        if (storedEvents.isEmpty()) {
            throw new EmptyResultDataAccessException("no events found for stream " + stream.getId() + " for version " + version, 1);
        }
        return storedEvents;
    }

    private List<StoredEvent<E>> loadEventsUptoTimestamp(EventStream stream, long timestamp) {
        List<StoredEvent<E>> storedEvents = jdbcTemplate.query(
                "select version, timestamp, data from event where event_stream_id = ? and timestamp <= ? order by sequence_number", 
                new StoredEventRowMapper(),
                stream.getId().toString(), new Date(timestamp));
        if (storedEvents.isEmpty()) {
            throw new EmptyResultDataAccessException("no events found for stream " + stream.getId() + " for timestamp " + timestamp, 1);
        }
        return storedEvents;
    }

    private void sendEventsToSink(EventStream stream, List<StoredEvent<E>> storedEvents, EventSink<E> sink) {
        List<E> events = new ArrayList<E>(storedEvents.size());
        for (StoredEvent<E> storedEvent : storedEvents) {
            events.add(storedEvent.getEvent());
        }
        StoredEvent<E> lastEvent = storedEvents.get(storedEvents.size() - 1);
        
        sink.setType(stream.getType());
        sink.setVersion(lastEvent.getVersion());
        sink.setTimestamp(lastEvent.getTimestamp());
        sink.setEvents(events);
    }

    private final class EventStreamRowMapper implements ParameterizedRowMapper<EventStream> {
        private final UUID streamId;

        private EventStreamRowMapper(UUID streamId) {
            this.streamId = streamId;
        }

        public EventStream mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new EventStream(
                    streamId,
                    rs.getString("type"),
                    rs.getLong("version"),
                    rs.getTimestamp("timestamp").getTime(),
                    rs.getInt("next_event_sequence"));
        }
    }

    private final class StoredEventRowMapper implements ParameterizedRowMapper<StoredEvent<E>> {
        public StoredEvent<E> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new StoredEvent<E>(
                    rs.getLong("version"),
                    rs.getTimestamp("timestamp").getTime(),
                    eventSerializer.deserialize(rs.getString("data")));
        }
    }

    public static class EventStream {
        
        private UUID id;
        private String type;
        private long version;
        private long timestamp;
        private int nextEventSequence;
        
        public EventStream(UUID id, String type, long version, long timestamp, int nextEventSequence) {
            this.id = id;
            this.type = type;
            this.version = version;
            this.timestamp = timestamp;
            this.nextEventSequence = nextEventSequence;
        }

        public UUID getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public long getVersion() {
            return version;
        }

        public long getTimestamp() {
            return timestamp;
        }
        
        public int getNextEventSequence() {
            return nextEventSequence;
        }

    }
    
    public static class StoredEvent<E> {
        
        private long version;
        private long timestamp;
        private E event;

        public StoredEvent(long version, long timestamp, E event) {
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

}
