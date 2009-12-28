package com.xebia.cqrs.domain;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.domain.VersionedId;
import com.xebia.cqrs.util.EqualsSupport;

public abstract class Event extends EqualsSupport {

    private final VersionedId aggregateRootId;
    private final Object entityId;
    
    public Event(VersionedId aggregateRootId, Object entityId) {
        Validate.notNull(aggregateRootId, "aggregateRootId is required");
        this.aggregateRootId = aggregateRootId;
        this.entityId = entityId;
    }

    public VersionedId getAggregateRootId() {
        return aggregateRootId;
    }
    
    public Object getEntityId() {
        return entityId;
    }

}
