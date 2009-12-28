package com.xebia.lottery.events;

import java.util.UUID;

import com.xebia.cqrs.domain.VersionedId;

public class LotteryTicketPrizeAwardedEvent extends LotteryEvent {

    private final String number;
    private final UUID customerId;
    private final double prizeAmount;

    public LotteryTicketPrizeAwardedEvent(VersionedId versionedId, String number, UUID customerId, double prizeAmount) {
        super(versionedId, number);
        this.number = number;
        this.customerId = customerId;
        this.prizeAmount = prizeAmount;
    }

    public String getNumber() {
        return number;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public double getPrizeAmount() {
        return prizeAmount;
    }
}
