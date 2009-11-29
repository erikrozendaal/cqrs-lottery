package com.xebia.lottery.events;

import java.util.UUID;

import com.xebia.lottery.shared.CustomerInfo;

public class CustomerCreatedEvent extends CustomerEvent {

    private static final long serialVersionUID = 1L;
    
    private final CustomerInfo info;

    public CustomerCreatedEvent(UUID customerId, long currentVersion, CustomerInfo info) {
        super(customerId, currentVersion);
        this.info = info;
    }

    public CustomerInfo getInfo() {
        return info;
    }
    
}
