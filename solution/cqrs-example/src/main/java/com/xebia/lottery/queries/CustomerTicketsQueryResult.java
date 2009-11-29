package com.xebia.lottery.queries;

import com.xebia.cqrs.domain.ValueObject;

public class CustomerTicketsQueryResult extends ValueObject {

    private static final long serialVersionUID = 1L;
    
    private final String ticketNumber;
    private final String lotteryName;
    private final String customerName;
    
    public CustomerTicketsQueryResult(String ticketNumber, String lotteryName, String customerName) {
        this.ticketNumber = ticketNumber;
        this.lotteryName = lotteryName;
        this.customerName = customerName;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getLotteryName() {
        return lotteryName;
    }

    public String getCustomerName() {
        return customerName;
    }
    
}
