package com.xebia.lottery.commands;

import java.util.UUID;

import org.apache.commons.lang.Validate;

import com.xebia.lottery.shared.LotteryInfo;

public class CreateLotteryCommand extends LotteryCommand {

    private static final long serialVersionUID = 1L;
    
    private final LotteryInfo info;

    public CreateLotteryCommand(LotteryInfo lotteryInfo) {
        super(UUID.randomUUID(), 0);
        Validate.notNull(lotteryInfo, "lotteryInfo is required");
        this.info = lotteryInfo;
    }

    public LotteryInfo getInfo() {
        return info;
    }
    
}
