package com.xebia.lottery.domain.aggregates;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.xebia.cqrs.domain.AggregateRoot;
import com.xebia.cqrs.events.Event;
import com.xebia.lottery.commands.ValidationError;
import com.xebia.lottery.events.LotteryCreatedEvent;
import com.xebia.lottery.events.LotteryTicketPurchasedEvent;
import com.xebia.lottery.shared.LotteryInfo;

public class Lottery extends AggregateRoot<UUID> {

    private static final Random RANDOM = new Random(42);
    private final Set<LotteryTicket> tickets = new HashSet<LotteryTicket>();
    private double ticketPrice;

    public Lottery() {
    }
    
    public Lottery(LotteryInfo info) {
        this(UUID.randomUUID(), info);
    }

    public Lottery(UUID lotteryId, LotteryInfo info) {
        apply(new LotteryCreatedEvent(lotteryId, getVersion(), info));
    }

    public void purchaseTicketForCustomer(Customer customer) {
        // Charge the customer and create the ticket
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
        setId(event.getLotteryId());
        this.ticketPrice = event.getInfo().getTicketPrice();
    }

    private void onTicketPurchasedEvent(LotteryTicketPurchasedEvent event) {
        tickets.add(new LotteryTicket(event.getTicketNumber(), event.getCustomerId()));
    }

}
