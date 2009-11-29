package com.xebia.lottery.domain.commandhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.Handler;
import com.xebia.cqrs.domain.Repository;
import com.xebia.lottery.commands.PurchaseLotteryTicketCommand;
import com.xebia.lottery.domain.aggregates.Customer;
import com.xebia.lottery.domain.aggregates.Lottery;

@Component
public class PurchaseLotteryTicketCommandHandler implements Handler<PurchaseLotteryTicketCommand> {

    @Autowired private Repository repository;
    
    public Class<PurchaseLotteryTicketCommand> getMessageType() {
        return PurchaseLotteryTicketCommand.class;
    }

    public void handleMessage(PurchaseLotteryTicketCommand command) {
        // Invoke lottery.purchaseTicketForCustomer etc...
    }

}
