package com.xebia.cqrs.eventstore.jdbc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.xebia.cqrs.domain.VersionedId;
import com.xebia.cqrs.eventstore.EventSerializer;
import com.xebia.cqrs.eventstore.EventSource;
import com.xebia.cqrs.eventstore.EventStore;


@Repository
public class JdbcEventStore<E> implements EventStore<E> {

    private static final Logger LOG = Logger.getLogger(JdbcEventStore.class);
    
    private final SimpleJdbcTemplate jdbcTemplate;
    
    private final EventSerializer<E> eventSerializer;

    @Autowired 
    public JdbcEventStore(SimpleJdbcTemplate jdbcTemplate, EventSerializer<E> eventSerializer) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventSerializer = eventSerializer;
    }
    
    public void storeEventSource(EventSource<? extends E> source) {
        List<? extends E> unsavedEvents = source.getUnsavedEvents();
        if (unsavedEvents.isEmpty()) {
            return;
        }
        
        if (source.getVersionedId().isForInitialVersion()) {
            insertEventSource(source, unsavedEvents);
        } else {
            updateEventSource(source, unsavedEvents);
        }
    }

    private void insertEventSource(EventSource<? extends E> source, List<? extends E> unsavedEvents) {
        JdbcEventSourceRow eventSourceRow = new JdbcEventSourceRow(source.getVersionedId().getId(), source.getClass());
        insertEventSourceRow(eventSourceRow, unsavedEvents.size());
        insertEvents(eventSourceRow, unsavedEvents);
        if (eventSourceRow.getNextEventSequenceNumber() != unsavedEvents.size()) {
            throw new IllegalStateException("event sequence mismatch: expected <" + unsavedEvents.size()
                    + ">, was <" + eventSourceRow.getNextEventSequenceNumber() + ">");
        }
    }

    private void insertEventSourceRow(JdbcEventSourceRow eventSourceRow, long initialNextEventSequenceNumber) {
        jdbcTemplate.update("insert into event_source (id, type, version, next_event_sequence_number) values (?, ?, ?, ?)", 
                eventSourceRow.getId(), eventSourceRow.getType().getName(), eventSourceRow.getVersion(), initialNextEventSequenceNumber);
    }

    private void updateEventSource(EventSource<? extends E> source, List<? extends E> unsavedEvents) {
        JdbcEventSourceRow eventSourceRow = loadEventSourceRow(source.getVersionedId().getId());
        insertEvents(eventSourceRow, unsavedEvents);
        updateEventSourceRow(source, eventSourceRow);
    }

    private JdbcEventSourceRow loadEventSourceRow(UUID eventSourceId) {
        return jdbcTemplate.queryForObject("select id, type, version, next_event_sequence_number from event_source where id = ?", 
                new JdbcEventSourceRowMapper(),
                eventSourceId.toString());
    }

    private void insertEvents(JdbcEventSourceRow eventSourceRow, List<? extends E> changes) {
        Date changesTimestamp = new Date();
        for (E event : changes) {
            insertEvent(eventSourceRow.getId(), eventSourceRow.nextEventSequenceNumber(), changesTimestamp, event);
        }
    }

    private void insertEvent(Object eventSourceId, long sequenceNumber, Date changesTimestamp, E event) {
        jdbcTemplate.update("insert into event (event_source_id, sequence_number, event_timestamp, data) values (?, ?, ?, ?)", eventSourceId,
                sequenceNumber, changesTimestamp, eventSerializer.serialize(event));
    }

    private void updateEventSourceRow(EventSource<? extends E> source, JdbcEventSourceRow jdbcEventSource) {
        long currentVersion = source.getVersionedId().getVersion();
        long previousVersion = currentVersion - 1;
        int updateCount = jdbcTemplate.update("update event_source set version = ?, next_event_sequence_number = ? where id = ? and version = ?", 
                currentVersion, jdbcEventSource.getNextEventSequenceNumber(),
                jdbcEventSource.getId(), previousVersion);
        if (updateCount != 1) {
            long actualVersion = jdbcTemplate.queryForLong("select version from event_source where id = ?", jdbcEventSource.getId());
            throwConcurrentModificationFailureException(jdbcEventSource, actualVersion, previousVersion);
        }
    }

    public <T extends EventSource<? super E>> T loadEventSource(Class<T> type, UUID eventSourceId) {
        try {
            JdbcEventSourceRow eventSourceRow = loadEventSourceRow(eventSourceId);
            List<E> history = loadEvents(eventSourceRow.getId());
            T result = instantiateEventSource(type, eventSourceRow, history);
            return result;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
    
    public <T extends EventSource<? super E>> T loadEventSource(Class<T> type, VersionedId eventSourceId) {
        try {
            JdbcEventSourceRow eventSourceRow = loadEventSourceRow(eventSourceId.getId());
            verifyVersion(eventSourceId.getVersion(), eventSourceRow);
            List<E> history = loadEvents(eventSourceId.getId());
            T result = instantiateEventSource(type, eventSourceRow, history);
            return result;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private <T extends EventSource<?>> void verifyVersion(long version, JdbcEventSourceRow eventSourceRow) {
        if (version != eventSourceRow.getVersion()) {
            throwConcurrentModificationFailureException(eventSourceRow, eventSourceRow.getVersion(), version);
        }
    }
    
    private List<E> loadEvents(Object eventSourceId) {
        return jdbcTemplate.query("select data from event where event_source_id = ? order by sequence_number", new JdbcEventDeserializerRowMapper(), eventSourceId);
    }

    private <T extends EventSource<? super E>> T instantiateEventSource(Class<T> expectedType, JdbcEventSourceRow eventSourceRow, List<E> history) {
        try {
            VersionedId id = VersionedId.forSpecificVersion(eventSourceRow.getId(), eventSourceRow.getVersion()).nextVersion();

            Constructor<T> constructor = expectedType.getConstructor(VersionedId.class);
            T result = constructor.newInstance(id);
            result.loadFromHistory(history);
            return result;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void throwConcurrentModificationFailureException(JdbcEventSourceRow jdbcEventSource, long actualVersion, long expectedVersion) {
        String msg = "concurrent modification of event source id '" + jdbcEventSource.getId() + "', type '" + jdbcEventSource.getType() + "'. Actual: " + actualVersion + ", expected: " + expectedVersion;
        throw new OptimisticLockingFailureException(msg);
    }

    private final class JdbcEventDeserializerRowMapper implements ParameterizedRowMapper<E> {
        public E mapRow(ResultSet rs, int rowNum) throws SQLException {
            return eventSerializer.deserialize(rs.getObject("data"));
        }
    }

    private final static class JdbcEventSourceRowMapper implements ParameterizedRowMapper<JdbcEventSourceRow> {
        public JdbcEventSourceRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                return new JdbcEventSourceRow(
                        UUID.fromString(rs.getString("id")), 
                        Class.forName(rs.getString("type")), 
                        rs.getLong("version"), 
                        rs.getLong("next_event_sequence_number"));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @PostConstruct 
    void init() {
        try {
            jdbcTemplate.update("drop table event if exists");
            jdbcTemplate.update("drop table event_source if exists");
            jdbcTemplate.update("create table event_source(id varchar primary key, type varchar not null, version bigint not null, next_event_sequence_number bigint not null)");
            jdbcTemplate.update("create table event(event_source_id varchar not null, sequence_number bigint not null, event_timestamp timestamp not null, data varchar not null, " 
                    + "primary key (event_source_id, sequence_number), foreign key (event_source_id) references event_source (id))");
        } catch (DataAccessException ex) {
            LOG.info("init database exception", ex);
        }
    }

}
