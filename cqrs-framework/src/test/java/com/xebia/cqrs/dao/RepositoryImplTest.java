package com.xebia.cqrs.dao;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.OptimisticLockingFailureException;

import com.xebia.cqrs.bus.Bus;
import com.xebia.cqrs.domain.AggregateRootNotFoundException;
import com.xebia.cqrs.domain.Event;
import com.xebia.cqrs.domain.FakeAggregateRoot;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.cqrs.eventstore.EventStore2;
import com.xebia.cqrs.eventstore.inmemory.InMemoryEventStore;


public class RepositoryImplTest {

    private static final VersionedId TEST_ID = VersionedId.random();
    
    private Bus bus;
    private FakeAggregateRoot aggregateRoot;
    private EventStore2<Event> eventStore;
    private RepositoryImpl subject;
    
    @Before
    public void setUp() {
        eventStore = new InMemoryEventStore<Event>();
        bus = createNiceMock(Bus.class);
        subject = new RepositoryImpl(eventStore, bus);

        aggregateRoot = new FakeAggregateRoot(TEST_ID);
        aggregateRoot.greetPerson("Erik");
        aggregateRoot.greetPerson("Sjors");
        subject.add(aggregateRoot);
    }
    
    @Test
    public void shouldFailToAddAggregateWithoutAnyUnsavedChanges() {
        FakeAggregateRoot a = new FakeAggregateRoot(VersionedId.random());
        try {
            subject.add(a);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }
    
    @Test
    public void shouldFailOnNonExistingAggregateRoot() {
        VersionedId id = VersionedId.random();
        try {
            subject.getByVersionedId(FakeAggregateRoot.class, id);
            fail("AggregateRootNotFoundException expected");
        } catch (AggregateRootNotFoundException expected) {
            assertEquals(FakeAggregateRoot.class.getName(), expected.getAggregateRootType());
            assertEquals(id.getId(), expected.getAggregateRootId());
        }
    }
    
    @Test
    public void shouldLoadAggregateRootFromEventStore() {
        subject.afterHandleMessage();
        
        FakeAggregateRoot result = subject.getByVersionedId(FakeAggregateRoot.class, TEST_ID);
        
        assertNotNull(result);
        assertEquals(aggregateRoot.getLastGreeting(), result.getLastGreeting());
    }
    
    @Test
    public void shouldLoadAggregateOnlyOnce() {
        FakeAggregateRoot a = subject.getById(FakeAggregateRoot.class, TEST_ID.getId());

        assertSame(aggregateRoot, a);
    }
    
    @Test
    public void shouldRejectDifferentAggregatesWithSameId() {
        FakeAggregateRoot a = aggregateRoot;
        FakeAggregateRoot b = new FakeAggregateRoot(TEST_ID);
        b.greetPerson("Jan");
        
        subject.add(a);
        try {
            subject.add(b);
            fail("IllegalStateException expected");
        } catch (IllegalStateException expected) {
        }
    }
    
    @Test
    public void shouldCheckAggregateVersionOnLoadFromSession() {
        try {
            subject.getByVersionedId(FakeAggregateRoot.class, TEST_ID.withVersion(0));
            fail("OptimisticLockingFailureException expected");
        } catch (OptimisticLockingFailureException expected) {
        }
    }
    
    @Test
    public void shouldStoreAddedAggregate() {
        aggregateRoot.greetPerson("Erik");
        replay(bus);
        
        subject.afterHandleMessage();
        
        verify(bus);
    }
    
    @Test
    public void shouldStoreLoadedAggregateWithNextVersion() {
        replay(bus);
        subject.afterHandleMessage();

        FakeAggregateRoot result = subject.getByVersionedId(FakeAggregateRoot.class, TEST_ID);
        result.greetPerson("Mark");
        subject.afterHandleMessage();
        
        FakeAggregateRoot loaded = subject.getByVersionedId(FakeAggregateRoot.class, TEST_ID.nextVersion());
        
        assertEquals("Hi Mark", loaded.getLastGreeting());
        verify(bus);
    }
    
    @Test
    public void shouldPublishChangeEventsOnSave() {
        aggregateRoot.greetPerson("Erik");

        bus.publish(eq(aggregateRoot.getUnsavedEvents())); expectLastCall();
        replay(bus);
        
        subject.afterHandleMessage();
        
        verify(bus);
    }
    
    @Test
    public void shouldReplyWithNotificationsOnSave() {
        aggregateRoot.greetPerson("Erik");

        bus.reply(eq(aggregateRoot.getNotifications())); expectLastCall();
        replay(bus);
        
        subject.afterHandleMessage();
        
        verify(bus);
    }
    
}
