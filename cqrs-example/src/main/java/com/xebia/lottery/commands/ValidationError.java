package com.xebia.lottery.commands;

import com.xebia.cqrs.domain.Notification;
import com.xebia.cqrs.util.EqualsSupport;

public class ValidationError extends EqualsSupport implements Notification {

    private final String errorMessage;

    public ValidationError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
}
