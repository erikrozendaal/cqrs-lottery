package com.xebia.cqrs.domain;

import com.xebia.cqrs.events.Event;

public class GreetingEvent extends Event {
    private static final long serialVersionUID = 1L;
    
    private final String message;

    public GreetingEvent(Object aggregateRootId, long aggregateRootVersion, String message) {
        super(aggregateRootId, aggregateRootVersion);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}