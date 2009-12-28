package com.xebia.lottery.events;

import com.xebia.cqrs.domain.VersionedId;

public class LotteryTicketPurchasedEvent extends LotteryEvent {

    private static final long serialVersionUID = 1L;
    
    private final VersionedId customerId;
    private final String ticketNumber;
    
    public LotteryTicketPurchasedEvent(VersionedId lotteryId, VersionedId customerId, String ticketNumber) {
        super(lotteryId, lotteryId.getId());
        this.customerId = customerId;
        this.ticketNumber = ticketNumber;
    }

    public VersionedId getCustomerId() {
        return customerId;
    }
    
    public String getTicketNumber() {
        return ticketNumber;
    }
    
}
