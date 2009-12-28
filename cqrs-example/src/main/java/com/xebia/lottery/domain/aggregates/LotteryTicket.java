package com.xebia.lottery.domain.aggregates;

import java.util.UUID;

import com.xebia.cqrs.domain.Aggregate;
import com.xebia.cqrs.domain.Entity;
import com.xebia.cqrs.domain.Event;
import com.xebia.lottery.events.LotteryTicketPrizeAwardedEvent;

public class LotteryTicket extends Entity<String> {

    private static final long serialVersionUID = 1L;

    private final String number;

    private final UUID customerId;

    private Double prizeAmount;
    
    public LotteryTicket(Aggregate aggregate, String number, UUID customerId) {
        super(aggregate, number);
        this.number = number;
        this.customerId = customerId;
    }

    public void win(double prizeAmount) {
        apply(new LotteryTicketPrizeAwardedEvent(aggregate.getVersionedId(), number, customerId, prizeAmount));
    }

    public String getId() {
        return number;
    }

    public void onEvent(Event event) {
        if (event instanceof LotteryTicketPrizeAwardedEvent) {
            onLotteryTicketPrizeAwarded((LotteryTicketPrizeAwardedEvent) event);
        } else {
            throw new IllegalArgumentException("unrecognized event: " + event);
        }
    }

    private void onLotteryTicketPrizeAwarded(LotteryTicketPrizeAwardedEvent event) {
        this.prizeAmount = event.getPrizeAmount();
    }

}
