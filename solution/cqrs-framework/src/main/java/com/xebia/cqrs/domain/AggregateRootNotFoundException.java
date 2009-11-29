package com.xebia.cqrs.domain;

import java.util.UUID;

public class AggregateRootNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String aggregateRootType;
    private final UUID aggregateRootId;

    public AggregateRootNotFoundException(String type, UUID id) {
        super("aggregate root " + type + " with id " + id);
        this.aggregateRootType = type;
        this.aggregateRootId = id;
    }
    
    public String getAggregateRootType() {
        return aggregateRootType;
    }
    
    public UUID getAggregateRootId() {
        return aggregateRootId;
    }

}
