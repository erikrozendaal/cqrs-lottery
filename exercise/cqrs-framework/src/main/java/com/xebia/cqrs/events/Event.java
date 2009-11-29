package com.xebia.cqrs.events;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.domain.ValueObject;

public abstract class Event extends ValueObject {

    private static final long serialVersionUID = 1L;
    
    private final Object aggregateRootId;
    private final long aggregateRootVersion;
    
    public Event(Object aggregateRootId, long aggregateRootVersion) {
        Validate.notNull(aggregateRootId, "aggregateRootId is required");
        this.aggregateRootId = aggregateRootId;
        this.aggregateRootVersion = aggregateRootVersion;
    }

    public Object getAggregateRootId() {
        return aggregateRootId;
    }

    public long getAggregateRootVersion() {
        return aggregateRootVersion;
    }
    
}
