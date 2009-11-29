package com.xebia.lottery.commands;

import com.xebia.cqrs.domain.ValueObject;
import com.xebia.cqrs.events.Notification;

public class ValidationError extends ValueObject implements Notification {

    private static final long serialVersionUID = 1L;
    
    private final String errorMessage;

    public ValidationError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
}
