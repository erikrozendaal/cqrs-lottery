package com.xebia.cqrs.domain;

public class AggregateRootNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String aggregateRootType;
    private final Object aggregateRootId;

    public AggregateRootNotFoundException(String type, Object id) {
        super("aggregate root " + type + " with id " + id);
        this.aggregateRootType = type;
        this.aggregateRootId = id;
    }
    
    public String getAggregateRootType() {
        return aggregateRootType;
    }
    
    public Object getAggregateRootId() {
        return aggregateRootId;
    }

}
