package com.xebia.cqrs.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;



public class AggregateRootTest {

    public static final VersionedId TEST_ID = VersionedId.random();
    
    private FakeAggregateRoot subject;

    @Before
    public void setUp() {
        subject = new FakeAggregateRoot();
        subject.greetPerson("Erik");
    }
    
    @Test
    public void shouldDispatchAppliedEvents() {
        assertEquals("Hi Erik", subject.getLastGreeting());
    }
    
    @Test
    public void shouldTrackUnsavedEvents() {
        assertEquals(new GreetingEvent(subject.getVersionedId(), "Hi Erik"), subject.getUnsavedEvents().iterator().next());
    }
    
    @Test
    public void shouldClearUnsavedChanges() {
        subject.clearUnsavedEvents();
        
        assertEquals(0, subject.getUnsavedEvents().size());
    }
    
}
