package com.xebia.lottery.events;

import java.util.UUID;

import org.apache.commons.lang.Validate;

import com.xebia.lottery.shared.LotteryInfo;

public class LotteryCreatedEvent extends LotteryEvent {

    private static final long serialVersionUID = 1L;
    
    private final LotteryInfo info;

    public LotteryCreatedEvent(UUID lotteryId, long currentVersion, LotteryInfo info) {
        super(lotteryId, currentVersion);
        Validate.notNull(info, "info is required");
        this.info = info;
    }

    public LotteryInfo getInfo() {
        return info;
    }

}
