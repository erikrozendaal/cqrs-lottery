package com.xebia.lottery.domain.aggregates;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import com.xebia.cqrs.domain.AggregateRoot;
import com.xebia.cqrs.domain.Event;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.commands.ValidationError;
import com.xebia.lottery.events.LotteryCreatedEvent;
import com.xebia.lottery.events.LotteryTicketPurchasedEvent;
import com.xebia.lottery.shared.LotteryInfo;

public class Lottery extends AggregateRoot {

    private static final Random RANDOM = new Random(42);
    private final Set<LotteryTicket> tickets = new HashSet<LotteryTicket>();
    private double ticketPrice;

    public Lottery(VersionedId id) {
        super(id);
    }
    
    public Lottery(VersionedId id, LotteryInfo info) {
        super(id);
        apply(new LotteryCreatedEvent(id,  info));
    }

    public void purchaseTicketForCustomer(Customer customer) {
        // Charge customer for ticket and ensure ticket is purchased
        throw new NotImplementedException("charge customer and create ticket");
    }

    private String generateTicketNumber() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }

    @Override
    protected void onEvent(Event event) {
        if (event instanceof LotteryCreatedEvent) {
            onLotteryCreatedEvent((LotteryCreatedEvent) event);
        } else if (event instanceof LotteryTicketPurchasedEvent) {
            onTicketPurchasedEvent((LotteryTicketPurchasedEvent) event);
        } else {
            throw new IllegalArgumentException("unrecognized event: " + event);
        }
    }

    private void onLotteryCreatedEvent(LotteryCreatedEvent event) {
        this.ticketPrice = event.getInfo().getTicketPrice();
    }

    private void onTicketPurchasedEvent(LotteryTicketPurchasedEvent event) {
        tickets.add(new LotteryTicket(event.getTicketNumber(), event.getCustomerId().getId()));
    }

}
