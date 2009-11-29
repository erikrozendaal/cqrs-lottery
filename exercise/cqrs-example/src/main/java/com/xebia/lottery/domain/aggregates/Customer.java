package com.xebia.lottery.domain.aggregates;

import java.util.UUID;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.domain.AggregateRoot;
import com.xebia.cqrs.events.Event;
import com.xebia.lottery.events.CustomerBalanceChangedEvent;
import com.xebia.lottery.events.CustomerCreatedEvent;
import com.xebia.lottery.shared.CustomerInfo;

public class Customer extends AggregateRoot<UUID> {

    private double accountBalance;

    public Customer() {
    }
    
    public Customer(CustomerInfo info, double initialAccountBalance) {
        this(UUID.randomUUID(), info, initialAccountBalance);
    }

    public Customer(UUID customerId, CustomerInfo customerInfo, double initialAccountBalance) {
        apply(new CustomerCreatedEvent(customerId, getVersion(), customerInfo));
        apply(new CustomerBalanceChangedEvent(customerId, getVersion(), 0.0, initialAccountBalance, initialAccountBalance));
    }
    
    public boolean isBalanceSufficient(double amount) {
        return this.accountBalance >= amount;
    }

    public void deductBalance(double amount) {
        Validate.isTrue(isBalanceSufficient(amount), "insufficient balance");
        apply(new CustomerBalanceChangedEvent(getId(), getVersion(), this.accountBalance, -amount, this.accountBalance - amount));
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
        setId(event.getCustomerId());
    }

    private void onCustomerBalanceChangedEvent(CustomerBalanceChangedEvent event) {
        this.accountBalance = event.getNewBalance();
    }

}
