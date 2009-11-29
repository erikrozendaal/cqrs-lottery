package com.xebia.cqrs.domain;

import com.xebia.cqrs.events.Notification;

public class GreetingNotification implements Notification {

    private final String message;

    public GreetingNotification(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }

}
