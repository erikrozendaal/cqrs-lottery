package com.xebia.lottery.commands;

import java.util.UUID;

import com.xebia.cqrs.commands.Command;

public abstract class LotteryCommand extends Command {

    private static final long serialVersionUID = 1L;
    
    private final UUID lotteryId;
    private final long lotteryVersion;
    
    public LotteryCommand(UUID lotteryId, long lotteryVersion) {
        this.lotteryId = lotteryId;
        this.lotteryVersion = lotteryVersion;
    }
    
    public UUID getLotteryId() {
        return lotteryId;
    }
    
    public long getLotteryVersion() {
        return lotteryVersion;
    }

}
