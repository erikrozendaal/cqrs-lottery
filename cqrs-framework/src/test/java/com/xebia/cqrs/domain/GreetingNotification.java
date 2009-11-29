package com.xebia.cqrs.domain;


public class GreetingNotification implements Notification {

    private final String message;

    public GreetingNotification(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }

}
