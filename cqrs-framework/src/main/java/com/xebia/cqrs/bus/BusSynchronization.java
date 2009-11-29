package com.xebia.cqrs.bus;

public interface BusSynchronization {

    void beforeHandleMessage();
    
    void afterHandleMessage();
    
}
