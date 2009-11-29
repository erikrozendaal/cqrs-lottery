package com.xebia.lottery.domain.commandhandlers;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.AbstractHandler;
import com.xebia.cqrs.domain.Repository;
import com.xebia.lottery.commands.CreateCustomerCommand;
import com.xebia.lottery.domain.aggregates.Customer;
import com.xebia.lottery.domain.aggregates.CustomerFactory;

@Component
public class CreateCustomerCommandHandler extends AbstractHandler<CreateCustomerCommand> {

    @Autowired
    private Repository repository;

    @Autowired
    private CustomerFactory customerFactory;

    public CreateCustomerCommandHandler() {
        super(CreateCustomerCommand.class);
    }

    public void handleMessage(CreateCustomerCommand message) {
        // Use the customer factory to create a customer
        // Save the customer when successfully created
        throw new NotImplementedException("create and save the customer to the repository here");
    }

}
