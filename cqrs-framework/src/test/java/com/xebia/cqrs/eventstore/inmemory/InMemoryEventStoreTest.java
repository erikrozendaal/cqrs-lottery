package com.xebia.cqrs.eventstore.inmemory;

import com.xebia.cqrs.eventstore.AbstractEventStoreTest;


public class InMemoryEventStoreTest extends AbstractEventStoreTest {

    @Override
    protected InMemoryEventStore<String> createSubject() {
        return new InMemoryEventStore<String>();
    }

}
