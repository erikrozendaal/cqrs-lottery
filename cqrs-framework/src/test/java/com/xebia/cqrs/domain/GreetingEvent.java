package com.xebia.cqrs.domain;


public class GreetingEvent extends Event {
    
    private final String message;

    public GreetingEvent(VersionedId aggregateRootId, String message) {
        super(aggregateRootId, aggregateRootId.getId());
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}