package com.xebia.cqrs.eventstore;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;



public abstract class AbstractEventStoreTest {
    
    private static final UUID ID_1 = UUID.randomUUID();
    private static final UUID ID_2 = UUID.randomUUID();
    
    private static final long T1 = 1000;
    private static final long T2 = 2000;

    private EventStore<String> subject;

    protected abstract EventStore<String> createSubject();
    
    @Before
    public void setUp() {
        subject = createSubject();
    }
    
    @Test
    public void should_create_event_stream_with_initial_version_and_events() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T1, asList("foo", "bar")));
        
        FakeEventSink sink = new FakeEventSink("type", 0, T1, asList("foo", "bar"));
        subject.loadEventsFromLatestStreamVersion(ID_1, sink);
        
        sink.verify();
    }
    
    @Test
    public void should_fail_to_create_stream_with_duplicate_id() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T1, asList("foo", "bar")));
        try {
            subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T2, asList("baz")));
            fail("DataIntegrityViolationException expected");
        } catch (DataIntegrityViolationException expected) {
        }
    }
    
    @Test
    public void should_store_events_into_stream() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T1, asList("foo", "bar")));
        subject.storeEventsIntoStream(ID_1, 0, new FakeEventSource2("type", 1, T2, asList("baz")));
        
        FakeEventSink sink = new FakeEventSink("type", 1, T2, asList("foo", "bar", "baz"));
        subject.loadEventsFromLatestStreamVersion(ID_1, sink);
        
        sink.verify();
    }
    
    @Test
    public void should_load_events_from_specific_stream_version() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T1, asList("foo", "bar")));
        subject.storeEventsIntoStream(ID_1, 0, new FakeEventSource2("type", 1, T2, asList("baz")));
        
        FakeEventSink sink = new FakeEventSink("type", 1, T2, asList("foo", "bar", "baz"));
        subject.loadEventsFromSpecificStreamVersion(ID_1, 1, sink);
        
        sink.verify();
    }
    
    @Test
    public void should_fail_to_load_events_from_specific_stream_version_when_expected_version_does_not_match_actual_version() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T1, asList("foo", "bar")));
        subject.storeEventsIntoStream(ID_1, 0, new FakeEventSource2("type", 1, T2, asList("baz")));
        
        try {
            subject.loadEventsFromSpecificStreamVersion(ID_1, 0, new FakeEventSink("type", 1, T2, asList("foo", "bar", "baz")));
            fail("OptimisticLockingFailureException expected");
        } catch (OptimisticLockingFailureException expected) {
        }
    }
    
    @Test
    public void should_store_separate_event_logs_for_different_event_streams() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T1, asList("foo", "bar")));
        subject.createEventStream(ID_2, new FakeEventSource2("type", 0, T2, asList("baz")));
        
        FakeEventSink sink1 = new FakeEventSink("type", 0, T1, asList("foo", "bar"));
        subject.loadEventsFromSpecificStreamVersion(ID_1, 0, sink1);
        FakeEventSink sink2 = new FakeEventSink("type", 0, T2, asList("baz"));
        subject.loadEventsFromSpecificStreamVersion(ID_2, 0, sink2);
        
        sink1.verify();
        sink2.verify();
    }
    
    @Test
    public void should_fail_to_store_events_into_stream_when_versions_do_not_match() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 1, T1, asList("foo", "bar")));
        try {
            subject.storeEventsIntoStream(ID_1, 0, new FakeEventSource2("type", 1, T2, asList("baz")));
            fail("OptimisticLockingFailureException expected");
        } catch (OptimisticLockingFailureException expected) {
        }
    }
    
    @Test
    public void should_check_optimistic_locking_error_before_decreasing_version_or_timestamp() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 5, T1, asList("foo", "bar")));
        try {
            subject.storeEventsIntoStream(ID_1, 4, new FakeEventSource2("type", 3, T2, asList("baz")));
            fail("OptimisticLockingFailureException expected");
        } catch (OptimisticLockingFailureException expected) {
        }
    }
    
    @Test
    public void should_fail_to_store_events_into_stream_when_new_version_is_before_previous_version() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 5, T1, asList("foo", "bar")));
        try {
            subject.storeEventsIntoStream(ID_1, 5, new FakeEventSource2("type", 4, T2, asList("baz")));
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }
    
    @Test
    public void should_fail_to_store_events_into_stream_when_new_timestamp_is_before_previous_timestamp() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T2, asList("foo", "bar")));
        try {
            subject.storeEventsIntoStream(ID_1, 0, new FakeEventSource2("type", 1, T1, asList("baz")));
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void should_fail_to_load_events_when_event_stream_version_does_not_match() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T1, asList("foo", "bar")));
        subject.storeEventsIntoStream(ID_1, 0, new FakeEventSource2("type", 1, T2, asList("baz")));
        
        try {
            subject.loadEventsFromSpecificStreamVersion(ID_1, 0, new FakeEventSink("type", 1, T2, asList("foo", "bar", "baz")));
            fail("OptimisticLockingFailureException expected");
        } catch (OptimisticLockingFailureException expected) {
        }
                
    }
    
    @Test
    public void should_fail_to_store_events_into_non_existing_event_stream() {
        try {
            subject.storeEventsIntoStream(ID_1, 0, new FakeEventSource2("type", 0, T1, asList("foo")));
            fail("EmptyResultDataAccessException expected");
        } catch (EmptyResultDataAccessException expected) {
        }
    }
    
    @Test
    public void should_fail_to_load_events_from_non_existing_event_stream() {
        try {
            subject.loadEventsFromSpecificStreamVersion(ID_1, 0, new FakeEventSink("type", 0, T1, asList("foo")));
            fail("EmptyResultDataAccessException expected");
        } catch (EmptyResultDataAccessException expected) {
        }
    }
    
    @Test
    public void should_load_events_from_stream_at_specific_version() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T1, asList("foo", "bar")));
        subject.storeEventsIntoStream(ID_1, 0, new FakeEventSource2("type", 1, T2, asList("baz")));
        
        FakeEventSink sink = new FakeEventSink("type", 0, T1, asList("foo", "bar"));
        subject.loadEventsFromStreamAtVersion(ID_1, 0, sink);
        
        sink.verify();
    }

    @Test
    public void should_load_all_events_from_stream_when_specified_version_is_higher_than_actual_version() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T1, asList("foo", "bar")));
        subject.storeEventsIntoStream(ID_1, 0, new FakeEventSource2("type", 1, T2, asList("baz")));
        
        FakeEventSink sink = new FakeEventSink("type", 1, T2, asList("foo", "bar", "baz"));
        subject.loadEventsFromStreamAtVersion(ID_1, 3, sink);
        
        sink.verify();
    }
    
    @Test
    public void should_fail_to_load_events_from_stream_when_requested_version_is_before_first_event_version() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 2, T1, asList("foo", "bar")));
        try {
            subject.loadEventsFromStreamAtVersion(ID_1, 1, new FakeEventSink("type", 0, T2, asList("foo", "bar")));
            fail("EmptyResultDataAccessException expected");
        } catch (EmptyResultDataAccessException expected) {
        }
    }
    
    @Test
    public void should_load_events_from_stream_at_specific_timestamp() {
        long t = T1 + 250;
        
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T1, asList("foo", "bar")));
        subject.storeEventsIntoStream(ID_1, 0, new FakeEventSource2("type", 1, T2, asList("baz")));
        
        FakeEventSink sink = new FakeEventSink("type", 0, T1, asList("foo", "bar"));
        subject.loadEventsFromStreamAtTimestamp(ID_1, t, sink);
        
        sink.verify();
    }
    
    @Test
    public void should_fail_to_load_events_from_stream_when_request_timestamp_is_before_first_event_timestamp() {
        subject.createEventStream(ID_1, new FakeEventSource2("type", 0, T2, asList("foo", "bar")));
        try {
            subject.loadEventsFromStreamAtTimestamp(ID_1, T1, new FakeEventSink("type", 0, T1, asList("foo", "bar")));
            fail("EmptyResultDataAccessException expected");
        } catch (EmptyResultDataAccessException expected) {
        }
    }
    
    public static class FakeEventSource2 implements EventSource<String> {

        private final String type;
        private final long version;
        private final long timestamp;
        private final List<String> events;
        
        public FakeEventSource2(String type, long version, long timestamp, List<String> events) {
            this.type = type;
            this.version = version;
            this.timestamp = timestamp;
            this.events = events;
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
        
        public List<? extends String> getEvents() {
            return events;
        }

    }
    
    public static final class FakeEventSink implements EventSink<String> {
        private final String expectedType;
        private final long expectedVersion;
        private final long expectedTimestamp;
        private final List<String> expectedEvents;
        private String actualType;
        private long actualVersion = -1;
        private long actualTimestamp = -1;
        private Iterable<? extends String> actualEvents;

        public FakeEventSink(String expectedType, long expectedVersion, long expectedTimestamp, List<String> expectedEvents) {
            this.expectedType = expectedType;
            this.expectedVersion = expectedVersion;
            this.expectedTimestamp = expectedTimestamp;
            this.expectedEvents = expectedEvents;
        }

        public void setType(String actualType) {
            this.actualType = actualType;
        }

        public void setVersion(long actualVersion) {
            assertNotNull("type must be set before version", actualType);
            this.actualVersion = actualVersion;
        }

        public void setTimestamp(long actualTimestamp) {
            assertNotNull("type must be set before version", actualType);
            this.actualTimestamp = actualTimestamp;
        }
        
        public void setEvents(Iterable<? extends String> actualEvents) {
            assertNotNull("type must be set before events", actualType);
            this.actualEvents = actualEvents;
        }

        public void verify() {
            assertEquals(expectedType, actualType);
            assertEquals(expectedVersion, actualVersion);
            assertEquals(expectedTimestamp, actualTimestamp);
            assertEquals(expectedEvents, actualEvents);
        }
        
    }
    
}
