package com.xebia.lottery.domain.commandhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.AbstractHandler;
import com.xebia.cqrs.domain.Repository;
import com.xebia.lottery.commands.CreateLotteryCommand;
import com.xebia.lottery.domain.aggregates.Lottery;

@Component
public class CreateLotteryCommandHandler extends AbstractHandler<CreateLotteryCommand> {

    @Autowired private Repository repository;
    
    public CreateLotteryCommandHandler() {
        super(CreateLotteryCommand.class);
    }

    public void handleMessage(CreateLotteryCommand message) {
        Lottery lottery = new Lottery(message.getInfo());
        repository.save(lottery);
    }

}
