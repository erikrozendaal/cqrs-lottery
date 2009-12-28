package com.xebia.lottery.events;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.shared.LotteryInfo;

public class LotteryCreatedEvent extends LotteryEvent {

    private static final long serialVersionUID = 1L;
    
    private final LotteryInfo info;

    public LotteryCreatedEvent(VersionedId lotteryId, LotteryInfo info) {
        super(lotteryId, lotteryId.getId());
        Validate.notNull(info, "info is required");
        this.info = info;
    }

    public LotteryInfo getInfo() {
        return info;
    }

}
