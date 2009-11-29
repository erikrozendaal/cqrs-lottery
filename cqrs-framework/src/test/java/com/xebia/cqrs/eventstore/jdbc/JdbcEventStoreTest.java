package com.xebia.cqrs.eventstore.jdbc;

import static org.junit.Assert.*;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.xebia.cqrs.domain.VersionedId;
import com.xebia.cqrs.eventstore.EventSerializer;
import com.xebia.cqrs.eventstore.EventSource;



public class JdbcEventStoreTest {
    
    private static final VersionedId INITIAL_ID = VersionedId.random();
    private static final VersionedId LATEST_ID = VersionedId.forLatestVersion(INITIAL_ID.getId());
    
    private SimpleJdbcTemplate jdbcTemplate;
    private JdbcEventStore<String> subject;
    
    private EventSource<String> eventSource = new FakeEventSource(INITIAL_ID);
    
    private EventSerializer<String> eventSerializer = new EventSerializer<String>() {

        public Object serialize(String event) {
            return event;
        }

        public String deserialize(Object serialized) {
            return (String) serialized;
        }
    };
    
    @Before
    public void setUp() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        initializeDatabase();
        subject = new JdbcEventStore<String>(jdbcTemplate, eventSerializer);
        subject.init();
    }
    
    @Test
    public void shouldSaveEvents() {
        subject.storeEventSource(eventSource);
        FakeEventSource result = subject.loadEventSource(FakeEventSource.class, LATEST_ID);
        
        assertEquals(1, jdbcTemplate.queryForInt("select count(*) from event_source"));
        assertEquals(0, jdbcTemplate.queryForInt("select version from event_source where id = ?", INITIAL_ID.getId()));
        assertEquals(2, jdbcTemplate.queryForInt("select next_event_sequence_number from event_source where id = ?", INITIAL_ID.getId()));
        assertEquals(2, jdbcTemplate.queryForInt("select count(*) from event"));
        
        assertEquals(Arrays.asList("foo", "bar"), result.getLoadedHistory());
    }
    
    @Test
    public void shouldIgnoreEventSourceWithoutUnsavedEventsOnSave() {
        eventSource.clearUnsavedEvents();
        
        subject.storeEventSource(eventSource);
        
        assertEquals(INITIAL_ID, eventSource.getVersionedId());
    }
    
    @Test
    public void shouldClearUnsavedEventsAndIncrementVersionOnSave() {
        subject.storeEventSource(eventSource);
        
        assertEquals(INITIAL_ID.nextVersion(), eventSource.getVersionedId());
        assertTrue(eventSource.getUnsavedEvents().isEmpty());
    }
    
    @Test
    public void shouldThrowConcurrencyExceptionOnVersionMismatch() {
        try {
            subject.storeEventSource(eventSource);
            subject.loadEventSource(FakeEventSource.class, INITIAL_ID.withVersion(4));
            fail("OptimisticLockingFailureException expected");
        } catch (OptimisticLockingFailureException expected) {
        }
    }

    private void initializeDatabase() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Driver driver = (Driver) Class.forName("org.hsqldb.jdbcDriver").newInstance();
        jdbcTemplate = new SimpleJdbcTemplate(new SimpleDriverDataSource(driver, "jdbc:hsqldb:mem:cqrs_test"));
    }

    private static class FakeEventSource implements EventSource<String> {

        private VersionedId id;
        private Iterable<? extends String> history;
        private List<String> unsavedEvents  = new ArrayList<String>();

        public FakeEventSource(VersionedId id) {
            this.id = id;
            unsavedEvents.add("foo");
            unsavedEvents.add("bar");
        }
        
        public VersionedId getVersionedId() {
            return id;
        }
        
        public Iterable<? extends String> getLoadedHistory() {
            return history;
        }

        public void incrementVersion() {
            id = id.nextVersion();
        }
        
        public List<String> getUnsavedEvents() {
            return unsavedEvents;
        }

        public void clearUnsavedEvents() {
            unsavedEvents.clear();
        }
        
        public void loadFromHistory(Iterable<? extends String> history) {
            this.history = history;
        }
    }

}
