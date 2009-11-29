package com.xebia.lottery.events;

import java.util.UUID;

import com.xebia.cqrs.events.Event;

public abstract class LotteryEvent extends Event {

    private static final long serialVersionUID = 1L;

    public LotteryEvent(UUID lotteryId, long lotteryVersion) {
        super(lotteryId, lotteryVersion);
    }
    
    public UUID getLotteryId() {
        return (UUID) getAggregateRootId();
    }
    
}
