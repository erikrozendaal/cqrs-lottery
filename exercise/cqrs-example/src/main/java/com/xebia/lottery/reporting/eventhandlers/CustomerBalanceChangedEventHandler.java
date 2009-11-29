package com.xebia.lottery.reporting.eventhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.AbstractHandler;
import com.xebia.lottery.events.CustomerBalanceChangedEvent;

@Component
public class CustomerBalanceChangedEventHandler extends AbstractHandler<CustomerBalanceChangedEvent> {

    @Autowired private SimpleJdbcTemplate simpleJdbcTemplate;
    
    public CustomerBalanceChangedEventHandler() {
        super(CustomerBalanceChangedEvent.class);
    }

    public void handleMessage(CustomerBalanceChangedEvent message) {
        simpleJdbcTemplate.update("update customer set version = ?, account_balance = ? where id = ?", 
                message.getAggregateRootVersion(), 
                message.getNewBalance(), 
                String.valueOf(message.getCustomerId()));
    }

}
