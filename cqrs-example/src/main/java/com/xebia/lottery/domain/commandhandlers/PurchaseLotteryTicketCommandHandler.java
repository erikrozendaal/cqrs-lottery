package com.xebia.lottery.domain.commandhandlers;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.Handler;
import com.xebia.cqrs.domain.Repository;
import com.xebia.lottery.commands.PurchaseTicketCommand;
import com.xebia.lottery.domain.aggregates.Customer;
import com.xebia.lottery.domain.aggregates.Lottery;

@Component
public class PurchaseLotteryTicketCommandHandler implements Handler<PurchaseTicketCommand> {

    @Autowired private Repository repository;
    
    public Class<PurchaseTicketCommand> getMessageType() {
        return PurchaseTicketCommand.class;
    }

    public void handleMessage(PurchaseTicketCommand command) {
        // Invoke lottery.purchaseTicketForCustomer etc...
        throw new NotImplementedException("handle purchase ticket command here");
    }

}
