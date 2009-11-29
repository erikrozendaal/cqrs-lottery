package com.xebia.lottery.queries;

import java.util.UUID;

import com.xebia.cqrs.domain.ValueObject;

public class CustomerAccountQueryResult extends ValueObject {

    private static final long serialVersionUID = 1L;
    
    private final UUID customerId;
    private final long customerVersion;
    private final String name;
    private final double currentBalance;

    public CustomerAccountQueryResult(UUID customerId, long customerVersion, String name, double currentBalance) {
        this.customerId = customerId;
        this.customerVersion = customerVersion;
        this.name = name;
        this.currentBalance = currentBalance;
    }

    public UUID getCustomerId() {
        return customerId;
    }
    
    public long getCustomerVersion() {
        return customerVersion;
    }
    
    public String getName() {
        return name;
    }
    
    public double getCurrentBalance() {
        return currentBalance;
    }
    
}
