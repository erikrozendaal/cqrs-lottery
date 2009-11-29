package com.xebia.lottery.events;

import com.xebia.cqrs.domain.Event;
import com.xebia.cqrs.domain.VersionedId;

public abstract class LotteryEvent extends Event {

    private static final long serialVersionUID = 1L;

    public LotteryEvent(VersionedId lotteryId) {
        super(lotteryId);
    }
    
    public VersionedId getLotteryId() {
        return getAggregateRootId();
    }
    
}
