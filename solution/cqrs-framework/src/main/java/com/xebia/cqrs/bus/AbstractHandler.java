package com.xebia.cqrs.bus;

public abstract class AbstractHandler<T> implements Handler<T> {

    private final Class<T> messageType;

    public AbstractHandler(Class<T> messageType) {
        this.messageType = messageType;
    }

    public final Class<T> getMessageType() {
        return messageType;
    }

}
