package com.xebia.cqrs.dao;

import static java.util.Arrays.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.xebia.cqrs.bus.Bus;
import com.xebia.cqrs.domain.AggregateRootNotFoundException;
import com.xebia.cqrs.domain.Event;
import com.xebia.cqrs.domain.FakeAggregateRoot;
import com.xebia.cqrs.domain.GreetingEvent;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.cqrs.eventstore.EventStore;


public class RepositoryImplTest {

    private static final VersionedId TEST_ID = VersionedId.random();
    
    private Bus bus;
    private FakeAggregateRoot aggregateRoot;
    private EventStore<Event> eventStore;
    private RepositoryImpl subject;
    
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        aggregateRoot = new FakeAggregateRoot(TEST_ID.withVersion(2));
        aggregateRoot.loadFromHistory(asList(
                new GreetingEvent(TEST_ID, "Hi Erik"),
                new GreetingEvent(TEST_ID, "Hi Sjors")));
        eventStore = createNiceMock(EventStore.class);
        bus = createNiceMock(Bus.class);
        subject = new RepositoryImpl(eventStore, bus);
    }
    
    @Test
    public void shouldFailOnNonExistingAggregateRoot() {
        expect(eventStore.loadEventSource(FakeAggregateRoot.class, TEST_ID)).andReturn(null);
        replay(eventStore);

        try {
            subject.getByVersionedId(FakeAggregateRoot.class, TEST_ID);
            fail("AggregateRootNotFoundException expected");
        } catch (AggregateRootNotFoundException expected) {
            verify(eventStore);
            assertEquals(FakeAggregateRoot.class.getName(), expected.getAggregateRootType());
            assertEquals(TEST_ID.getId(), expected.getAggregateRootId());
        }
    }
    
    @Test
    public void shouldLoadAggregateRootFromEventStore() {
        expect(eventStore.loadEventSource(FakeAggregateRoot.class, TEST_ID)).andReturn(aggregateRoot);
        replay(eventStore);
        
        FakeAggregateRoot result = subject.getByVersionedId(FakeAggregateRoot.class, TEST_ID);

        verify(eventStore);
        assertSame(aggregateRoot, result);
    }
    
    @Test
    public void shouldStoreAndClearUnsavedEvents() {
        aggregateRoot.greetPerson("Erik");

        eventStore.storeEventSource(same(aggregateRoot)); expectLastCall();
        replay(eventStore, bus);
        
        subject.save(aggregateRoot);
        
        verify(eventStore, bus);
        assertTrue(aggregateRoot.getUnsavedEvents().isEmpty());
    }
    
    @Test
    public void shouldPublishChangeEventsOnSave() {
        aggregateRoot.greetPerson("Erik");

        bus.publish(eq(aggregateRoot.getUnsavedEvents())); expectLastCall();
        replay(eventStore, bus);
        
        subject.save(aggregateRoot);
        
        verify(eventStore, bus);
    }
    
    @Test
    public void shouldReplyWithNotificationsOnSave() {
        aggregateRoot.greetPerson("Erik");

        bus.reply(eq(aggregateRoot.getNotifications())); expectLastCall();
        replay(eventStore, bus);
        
        subject.save(aggregateRoot);
        
        verify(eventStore, bus);
    }
    
}
