package com.xebia.lottery.queries;

import com.xebia.cqrs.domain.ValueObject;
import com.xebia.cqrs.domain.VersionedId;

public class CustomerAccountQueryResult extends ValueObject {

    private static final long serialVersionUID = 1L;
    
    private final VersionedId customerId;
    private final String customerName;
    private final double currentBalance;

    public CustomerAccountQueryResult(VersionedId customerId, String customerName, double currentBalance) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.currentBalance = currentBalance;
    }

    public VersionedId getCustomerId() {
        return customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public double getCurrentBalance() {
        return currentBalance;
    }
    
}
