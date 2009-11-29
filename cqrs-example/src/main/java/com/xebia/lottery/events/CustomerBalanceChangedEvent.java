package com.xebia.lottery.events;

import com.xebia.cqrs.domain.VersionedId;

public class CustomerBalanceChangedEvent extends CustomerEvent {

    private static final long serialVersionUID = 1L;
    
    private final double oldBalance;
    private final double amountChanged;
    private final double newBalance;

    public CustomerBalanceChangedEvent(VersionedId customerId, double oldBalance, double amountChanged, double newBalance) {
        super(customerId);
        this.oldBalance = oldBalance;
        this.amountChanged = amountChanged;
        this.newBalance = newBalance;
    }

    public double getOldBalance() {
        return oldBalance;
    }

    public double getAmountChanged() {
        return amountChanged;
    }

    public double getNewBalance() {
        return newBalance;
    }
    
}
