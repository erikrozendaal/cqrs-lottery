package com.xebia.lottery.commands;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.domain.VersionedId;

public abstract class LotteryCommand extends Command {

    private final VersionedId lotteryId;
    
    public LotteryCommand(VersionedId lotteryId) {
        Validate.notNull(lotteryId, "lotteryId is required");
        this.lotteryId = lotteryId;
    }
    
    public VersionedId getLotteryId() {
        return lotteryId;
    }
    
}
