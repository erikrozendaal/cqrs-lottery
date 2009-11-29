package com.xebia.lottery.domain.aggregates;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.domain.AggregateRoot;
import com.xebia.cqrs.domain.Event;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.events.CustomerBalanceChangedEvent;
import com.xebia.lottery.events.CustomerCreatedEvent;
import com.xebia.lottery.shared.CustomerInfo;

public class Customer extends AggregateRoot {

    private double accountBalance;

    public Customer(VersionedId id) {
        super(id);
    }
    
    public Customer(VersionedId customerId, CustomerInfo customerInfo, double initialAccountBalance) {
        super(customerId);
        apply(new CustomerCreatedEvent(customerId, customerInfo));
        apply(new CustomerBalanceChangedEvent(customerId, 0.0, initialAccountBalance, initialAccountBalance));
    }
    
    public boolean isBalanceSufficient(double amount) {
        return this.accountBalance >= amount;
    }

    public void deductBalance(double amount) {
        Validate.isTrue(isBalanceSufficient(amount), "insufficient balance");
        apply(new CustomerBalanceChangedEvent(getVersionedId(), this.accountBalance, -amount, this.accountBalance - amount));
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof CustomerCreatedEvent) {
            onCustomerCreatedEvent((CustomerCreatedEvent) event);
        } else if (event instanceof CustomerBalanceChangedEvent) {
            onCustomerBalanceChangedEvent((CustomerBalanceChangedEvent) event);
        } else {
            throw new IllegalArgumentException("unrecognized event: " + event);
        }
    }

    private void onCustomerCreatedEvent(CustomerCreatedEvent event) {
    }

    private void onCustomerBalanceChangedEvent(CustomerBalanceChangedEvent event) {
        this.accountBalance = event.getNewBalance();
    }

}
