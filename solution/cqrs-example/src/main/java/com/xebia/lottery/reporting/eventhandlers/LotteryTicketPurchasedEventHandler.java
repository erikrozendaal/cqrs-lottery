package com.xebia.lottery.reporting.eventhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.AbstractHandler;
import com.xebia.lottery.events.LotteryTicketPurchasedEvent;

@Component
public class LotteryTicketPurchasedEventHandler extends AbstractHandler<LotteryTicketPurchasedEvent> {

    @Autowired
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    public LotteryTicketPurchasedEventHandler() {
        super(LotteryTicketPurchasedEvent.class);
    }

    public void handleMessage(LotteryTicketPurchasedEvent message) {
        simpleJdbcTemplate.update("insert into ticket (number, lottery_id, customer_id) values (?, ?, ?)", 
                message.getTicketNumber(), 
                message.getAggregateRootId().getId(), 
                message.getCustomerId().getId());
        simpleJdbcTemplate.update("update lottery set version = ? where id = ?", 
                message.getAggregateRootId().getVersion(), 
                message.getAggregateRootId().getId());
    }

}
