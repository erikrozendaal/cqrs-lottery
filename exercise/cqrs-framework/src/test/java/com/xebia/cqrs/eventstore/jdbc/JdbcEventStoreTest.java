package com.xebia.cqrs.eventstore.jdbc;

import static org.junit.Assert.*;

import java.sql.Driver;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.xebia.cqrs.eventstore.EventSerializer;
import com.xebia.cqrs.eventstore.EventSource;



public class JdbcEventStoreTest {
    
    private SimpleJdbcTemplate jdbcTemplate;
    private JdbcEventStore<String> subject;
    
    private EventSource<String> eventSource = new FakeEventSource();
    
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
        FakeEventSource result = subject.loadEventSource(FakeEventSource.class, 123, 0);
        
        assertEquals(1, jdbcTemplate.queryForInt("select count(*) from event_source"));
        assertEquals(0, jdbcTemplate.queryForInt("select version from event_source where id = 123"));
        assertEquals(2, jdbcTemplate.queryForInt("select next_event_sequence_number from event_source where id = 123"));
        assertEquals(2, jdbcTemplate.queryForInt("select count(*) from event"));
        
        assertEquals(Arrays.asList("foo", "bar"), result.getLoadedHistory());
    }
    
    @Test
    public void shouldThrowConcurrencyExceptionOnVersionMismatch() {
        try {
            subject.storeEventSource(eventSource);
            subject.loadEventSource(FakeEventSource.class, 123, 4);
            fail("OptimisticLockingFailureException expected");
        } catch (OptimisticLockingFailureException expected) {
        }
    }

    private void initializeDatabase() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Driver driver = (Driver) Class.forName("org.hsqldb.jdbcDriver").newInstance();
        jdbcTemplate = new SimpleJdbcTemplate(new SimpleDriverDataSource(driver, "jdbc:hsqldb:mem:cqrs_test"));
    }

    private static class FakeEventSource implements EventSource<String> {
        private long version = 0;
        private Iterable<? extends String> history;

        public FakeEventSource() {
        }
        
        public Object getId() {
            return 123;
        }

        public Iterable<? extends String> getLoadedHistory() {
            return history;
        }

        public long getVersion() {
            return version;
        }

        public void setVersion(long version) {
            this.version = version;
        }

        public List<String> getUnsavedEvents() {
            return Arrays.asList("foo", "bar");
        }

        public void loadFromHistory(Iterable<? extends String> history) {
            this.history = history;
        }
    }

}
