package com.xebia.lottery.commands;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.shared.LotteryInfo;

public class CreateLotteryCommand extends LotteryCommand {

    private final LotteryInfo info;

    public CreateLotteryCommand(VersionedId lotteryId, LotteryInfo lotteryInfo) {
        super(lotteryId);
        Validate.notNull(lotteryInfo, "lotteryInfo is required");
        this.info = lotteryInfo;
    }

    public LotteryInfo getInfo() {
        return info;
    }
    
}
