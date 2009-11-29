package com.xebia.lottery.commands;

import com.xebia.cqrs.domain.VersionedId;

public class PurchaseTicketCommand extends Command {

    private final VersionedId lotteryId;
    private final VersionedId customerId;

    public PurchaseTicketCommand(VersionedId lotteryId, VersionedId customerId) {
        this.lotteryId = lotteryId;
        this.customerId = customerId;
    }

    public VersionedId getLotteryId() {
        return lotteryId;
    }

    public VersionedId getCustomerId() {
        return customerId;
    }

}
