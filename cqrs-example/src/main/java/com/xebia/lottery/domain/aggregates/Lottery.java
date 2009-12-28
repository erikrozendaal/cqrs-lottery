package com.xebia.lottery.domain.aggregates;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
    private double prizeAmount;

    public Lottery(VersionedId id) {
        super(id);
    }
    
    public Lottery(VersionedId id, LotteryInfo info) {
        super(id);
        apply(new LotteryCreatedEvent(id,  info));
    }

    public void purchaseTicketForCustomer(Customer customer) {
        if (!customer.isBalanceSufficient(this.ticketPrice)) {
            notify(new ValidationError("insufficient account balance to purchase ticket"));
            return;
        }
        
        customer.deductBalance(this.ticketPrice);
        apply(new LotteryTicketPurchasedEvent(aggregate.getVersionedId(), customer.getVersionedId(), generateTicketNumber()));
    }

    public void draw() {
        LotteryTicket winningTicket = tickets.iterator().next();
        winningTicket.win(prizeAmount);
    }
    
    private String generateTicketNumber() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }

    public void onEvent(Event event) {
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
        this.prizeAmount = event.getInfo().getPrizeAmount();
    }

    private void onTicketPurchasedEvent(LotteryTicketPurchasedEvent event) {
        tickets.add(new LotteryTicket(aggregate, event.getTicketNumber(), event.getCustomerId().getId()));
    }

}
