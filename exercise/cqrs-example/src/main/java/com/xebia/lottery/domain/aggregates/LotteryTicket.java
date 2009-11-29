package com.xebia.lottery.domain.aggregates;

import java.util.UUID;

import com.xebia.cqrs.domain.ValueObject;

public class LotteryTicket extends ValueObject {

    private static final long serialVersionUID = 1L;

    private final String number;

    private final UUID customerId;
    
    public LotteryTicket(String number, UUID customerId) {
        this.number = number;
        this.customerId = customerId;
    }

    public String getNumber() {
        return number;
    }

    public UUID getCustomerId() {
        return customerId;
    }

}
