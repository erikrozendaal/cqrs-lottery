package com.xebia.lottery.events;

import java.util.UUID;

public class LotteryTicketPurchasedEvent extends LotteryEvent {

    private static final long serialVersionUID = 1L;
    
    private final UUID customerId;
    private final String ticketNumber;
    
    public LotteryTicketPurchasedEvent(UUID lotteryId, long lotteryVersion, UUID customerId, String ticketNumber) {
        super(lotteryId, lotteryVersion);
        this.customerId = customerId;
        this.ticketNumber = ticketNumber;
    }

    public UUID getCustomerId() {
        return customerId;
    }
    
    public String getTicketNumber() {
        return ticketNumber;
    }
    
}
