package com.xebia.lottery.shared;

import java.util.Date;

import com.xebia.cqrs.domain.ValueObject;

public class LotteryInfo extends ValueObject {

    private static final long serialVersionUID = 1L;
    
    private final String name;
    private final Date drawingTimestamp;
    private final double prizeAmount;
    private final double ticketPrice;
    
    public LotteryInfo(String name, Date drawingTimestamp, double prizeAmount, double ticketPrice) {
        this.name = name;
        this.drawingTimestamp = drawingTimestamp;
        this.prizeAmount = prizeAmount;
        this.ticketPrice = ticketPrice;
    }
    
    public String getName() {
        return name;
    }

    public Date getDrawingTimestamp() {
        return drawingTimestamp;
    }

    public double getPrizeAmount() {
        return prizeAmount;
    }
    
    public double getTicketPrice() {
        return ticketPrice;
    }
    
}
