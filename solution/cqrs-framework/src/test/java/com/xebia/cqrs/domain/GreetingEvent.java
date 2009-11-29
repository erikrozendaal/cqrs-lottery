package com.xebia.cqrs.domain;


public class GreetingEvent extends Event {
    private static final long serialVersionUID = 1L;
    
    private final String message;

    public GreetingEvent(VersionedId aggregateRootId, String message) {
        super(aggregateRootId);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}