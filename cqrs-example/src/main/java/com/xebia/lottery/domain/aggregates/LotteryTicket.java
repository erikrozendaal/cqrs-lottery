package com.xebia.lottery.domain.aggregates;

import com.xebia.cqrs.domain.ValueObject;

public class LotteryTicket extends ValueObject {

    private static final long serialVersionUID = 1L;

    private final String number;

    private final Object customerId;
    
    public LotteryTicket(String number, Object customerId) {
        this.number = number;
        this.customerId = customerId;
    }

    public String getNumber() {
        return number;
    }

    public Object getCustomerId() {
        return customerId;
    }

}
