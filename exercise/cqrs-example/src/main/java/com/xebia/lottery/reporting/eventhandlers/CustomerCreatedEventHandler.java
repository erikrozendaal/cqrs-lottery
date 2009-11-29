package com.xebia.lottery.reporting.eventhandlers;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.AbstractHandler;
import com.xebia.lottery.events.CustomerCreatedEvent;
import com.xebia.lottery.shared.CustomerInfo;

@Component
public class CustomerCreatedEventHandler extends AbstractHandler<CustomerCreatedEvent> {

    @Autowired
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    public CustomerCreatedEventHandler() {
        super(CustomerCreatedEvent.class);
    }

    public void handleMessage(CustomerCreatedEvent message) {
        throw new NotImplementedException("insert the customer into the reporting database here");
//        CustomerInfo info = message.getInfo();
//        simpleJdbcTemplate.update("insert into customer(id, version, name, account_balance, email, street_name, house_number, postal_code, city, country) values (?, ?, ?, 0, ?, ?, ?, ?, ?, ?)", 
//                message.getAggregateRootId(), 
//                message.getAggregateRootVersion(), 
//                info.getName(), 
//                info.getEmail(), 
//                info.getAddress().getStreetName(),
//                info.getAddress().getHouseNumber(),
//                info.getAddress().getPostalCode(),
//                info.getAddress().getCity(),
//                info.getAddress().getCountry());
    }

}
