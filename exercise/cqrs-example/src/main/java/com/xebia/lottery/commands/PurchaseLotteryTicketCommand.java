package com.xebia.lottery.commands;

import java.util.UUID;

import com.xebia.cqrs.commands.Command;

public class PurchaseLotteryTicketCommand extends Command {

    private static final long serialVersionUID = 1L;
    
    private final UUID lotteryId;
    private final long lotteryVersion;
    private final UUID customerId;
    private final long customerVersion;

    public PurchaseLotteryTicketCommand(UUID lotteryId, long lotteryVersion, UUID customerId, long customerVersion) {
        this.lotteryId = lotteryId;
        this.lotteryVersion = lotteryVersion;
        this.customerId = customerId;
        this.customerVersion = customerVersion;
    }

    public UUID getLotteryId() {
        return lotteryId;
    }

    public long getLotteryVersion() {
        return lotteryVersion;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public long getCustomerVersion() {
        return customerVersion;
    }
    
}
