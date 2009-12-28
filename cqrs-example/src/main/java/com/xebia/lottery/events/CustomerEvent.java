package com.xebia.lottery.events;

import com.xebia.cqrs.domain.Event;
import com.xebia.cqrs.domain.VersionedId;

public abstract class CustomerEvent extends Event {

    private static final long serialVersionUID = 1L;

    public CustomerEvent(VersionedId customerId) {
        super(customerId, customerId.getId());
    }

    public VersionedId getCustomerId() {
        return getAggregateRootId();
    }
    
}
