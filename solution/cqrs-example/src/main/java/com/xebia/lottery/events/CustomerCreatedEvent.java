package com.xebia.lottery.events;

import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.shared.CustomerInfo;

public class CustomerCreatedEvent extends CustomerEvent {

    private static final long serialVersionUID = 1L;
    
    private final CustomerInfo info;

    public CustomerCreatedEvent(VersionedId customerId, CustomerInfo info) {
        super(customerId);
        this.info = info;
    }

    public CustomerInfo getInfo() {
        return info;
    }

}
