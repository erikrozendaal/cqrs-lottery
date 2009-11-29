package com.xebia.cqrs.domain;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.domain.VersionedId;
import com.xebia.cqrs.util.EqualsSupport;

public abstract class Event extends EqualsSupport {

    private final VersionedId aggregateRootId;
    
    public Event(VersionedId aggregateRootId) {
        Validate.notNull(aggregateRootId, "aggregateRootId is required");
        this.aggregateRootId = aggregateRootId;
    }

    public VersionedId getAggregateRootId() {
        return aggregateRootId;
    }

}
