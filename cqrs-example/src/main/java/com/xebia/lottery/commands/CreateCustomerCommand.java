package com.xebia.lottery.commands;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.shared.CustomerInfo;

public class CreateCustomerCommand extends Command {

    private final VersionedId customerId;
    private final CustomerInfo info;
    private final double initialAccountBalance;

    public CreateCustomerCommand(VersionedId customerId, CustomerInfo info, double initialAccountBalance) {
        Validate.notNull(customerId, "customerId is required");
        Validate.notNull(info, "info is required");
        this.customerId = customerId;
        this.info = info;
        this.initialAccountBalance = initialAccountBalance;
    }
    
    public VersionedId getCustomerId() {
        return customerId;
    }
    
    public CustomerInfo getInfo() {
        return info;
    }

    public double getInitialAccountBalance() {
        return initialAccountBalance;
    }
    
}
