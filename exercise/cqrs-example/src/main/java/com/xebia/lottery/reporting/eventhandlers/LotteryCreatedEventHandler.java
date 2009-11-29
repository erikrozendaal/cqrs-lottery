package com.xebia.lottery.reporting.eventhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.AbstractHandler;
import com.xebia.lottery.events.LotteryCreatedEvent;
import com.xebia.lottery.shared.LotteryInfo;

@Component
public class LotteryCreatedEventHandler extends AbstractHandler<LotteryCreatedEvent> {

    @Autowired
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    public LotteryCreatedEventHandler() {
        super(LotteryCreatedEvent.class);
    }

    public void handleMessage(LotteryCreatedEvent message) {
        LotteryInfo info = message.getInfo();
        simpleJdbcTemplate.update("insert into lottery(id, version, name, drawing_timestamp, prize_amount, ticket_price) values (?, ?, ?, ?, ?, ?)", 
                message.getAggregateRootId(), 
                message.getAggregateRootVersion(), 
                info.getName(), 
                info.getDrawingTimestamp(), 
                info.getPrizeAmount(),
                info.getTicketPrice());
    }

}
