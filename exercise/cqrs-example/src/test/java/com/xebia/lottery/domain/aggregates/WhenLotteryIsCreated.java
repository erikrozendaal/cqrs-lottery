package com.xebia.lottery.domain.aggregates;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.xebia.lottery.events.LotteryCreatedEvent;
import com.xebia.lottery.shared.LotteryInfo;


public class WhenLotteryIsCreated extends BddTestCase {
    
    public static final UUID LOTTERY_ID = UUID.randomUUID();
    public static final LotteryInfo LOTTER_INFO = new LotteryInfo("lottery", new Date(System.currentTimeMillis() + 100000), 1000.00, 25);
    
    private Lottery subject;
    
    @Override
    protected void when() {
        subject = new Lottery(LOTTERY_ID, LOTTER_INFO);
    }
    
    @Test
    public void shouldRaiseLotteryCreatedEvent() {
        assertChange(subject, new LotteryCreatedEvent(LOTTERY_ID, 0, LOTTER_INFO));
    }

}
