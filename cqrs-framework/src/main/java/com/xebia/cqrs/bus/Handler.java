package com.xebia.cqrs.bus;

public interface Handler<T> {

    Class<T> getMessageType();
    
    void handleMessage(T message) throws Exception;
    
}
