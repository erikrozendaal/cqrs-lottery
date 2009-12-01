package com.xebia.lottery.domain.commandhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.AbstractHandler;
import com.xebia.cqrs.domain.Repository;
import com.xebia.lottery.commands.PurchaseTicketCommand;
import com.xebia.lottery.domain.aggregates.Customer;
import com.xebia.lottery.domain.aggregates.Lottery;

@Component
public class PurchaseLotteryTicketCommandHandler extends AbstractHandler<PurchaseTicketCommand> {

    private final Repository repository;
    
    @Autowired
    public PurchaseLotteryTicketCommandHandler(Repository repository) {
        super(PurchaseTicketCommand.class);
        this.repository = repository;
    }

    public void handleMessage(PurchaseTicketCommand command) {
        Lottery lottery = repository.getByVersionedId(Lottery.class, command.getLotteryId());
        Customer customer = repository.getByVersionedId(Customer.class, command.getCustomerId());
        lottery.purchaseTicketForCustomer(customer);
    }

}
