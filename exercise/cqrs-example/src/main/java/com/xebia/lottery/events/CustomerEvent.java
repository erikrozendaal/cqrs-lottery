package com.xebia.lottery.events;

import java.util.UUID;

import com.xebia.cqrs.events.Event;

public abstract class CustomerEvent extends Event {

    private static final long serialVersionUID = 1L;

    public CustomerEvent(UUID customerId, long version) {
        super(customerId, version);
    }
    
    public UUID getCustomerId() {
        return (UUID) getAggregateRootId();
    }
    
}
