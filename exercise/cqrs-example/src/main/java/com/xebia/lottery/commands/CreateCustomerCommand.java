package com.xebia.lottery.commands;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.commands.Command;
import com.xebia.lottery.shared.CustomerInfo;

public class CreateCustomerCommand extends Command {

    private static final long serialVersionUID = 1L;
    
    private final CustomerInfo info;
    private final double initialAccountBalance;

    public CreateCustomerCommand(CustomerInfo info, double initialAccountBalance) {
        Validate.notNull(info, "info is required");
        this.info = info;
        this.initialAccountBalance = initialAccountBalance;
    }
    
    public CustomerInfo getInfo() {
        return info;
    }

    public double getInitialAccountBalance() {
        return initialAccountBalance;
    }
    
}
