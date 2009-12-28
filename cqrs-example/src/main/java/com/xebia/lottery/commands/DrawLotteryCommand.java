package com.xebia.lottery.commands;

import com.xebia.cqrs.domain.VersionedId;

public class DrawLotteryCommand extends LotteryCommand {

    public DrawLotteryCommand(VersionedId lotteryId) {
        super(lotteryId);
    }

}
