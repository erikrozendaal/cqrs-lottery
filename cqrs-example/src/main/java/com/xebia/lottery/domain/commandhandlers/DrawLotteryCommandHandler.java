package com.xebia.lottery.domain.commandhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.AbstractHandler;
import com.xebia.cqrs.domain.Repository;
import com.xebia.lottery.commands.DrawLotteryCommand;
import com.xebia.lottery.domain.aggregates.Lottery;

@Component
public class DrawLotteryCommandHandler extends AbstractHandler<DrawLotteryCommand> {

    @Autowired private Repository repository;
    
    public DrawLotteryCommandHandler() {
        super(DrawLotteryCommand.class);
    }

    public void handleMessage(DrawLotteryCommand message) {
        Lottery lottery = repository.getByVersionedId(Lottery.class, message.getLotteryId());
        lottery.draw();
    }

}
