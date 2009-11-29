package com.xebia.lottery.reporting.queries;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xebia.lottery.domain.aggregates.WhenCustomerIsCreated;
import com.xebia.lottery.events.CustomerBalanceChangedEvent;
import com.xebia.lottery.events.CustomerCreatedEvent;
import com.xebia.lottery.events.LotteryCreatedEvent;
import com.xebia.lottery.queries.CustomerAccountQueryResult;
import com.xebia.lottery.queries.LotteryInfoQueryResult;
import com.xebia.lottery.reporting.eventhandlers.CustomerBalanceChangedEventHandler;
import com.xebia.lottery.reporting.eventhandlers.CustomerCreatedEventHandler;
import com.xebia.lottery.reporting.eventhandlers.LotteryCreatedEventHandler;
import com.xebia.lottery.shared.LotteryInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-application-context.xml")
public class JdbcLotteryQueryServiceTest {
    
    private static final UUID LOTTERY_ID = UUID.randomUUID();
    private static final LotteryInfo LOTTERY_INFO = new LotteryInfo("lottery", new Date(System.currentTimeMillis() + 100000), 1000.0, 15);

    @Autowired private JdbcLotteryQueryService lotteryQueryService;
    @Autowired private LotteryCreatedEventHandler lotteryCreatedEventHandler;
    @Autowired private CustomerCreatedEventHandler customerCreatedEventHandler;
    @Autowired private CustomerBalanceChangedEventHandler customerBalanceChangedEventHandler;
    
    @Test
    public void findUpcomingLotteries() {
        assertTrue(lotteryQueryService.findUpcomingLotteries().isEmpty());
        
        lotteryCreatedEventHandler.handleMessage(new LotteryCreatedEvent(LOTTERY_ID, 0, LOTTERY_INFO));
        
        List<LotteryInfoQueryResult> upcomingLotteries = lotteryQueryService.findUpcomingLotteries();
        assertEquals(1, upcomingLotteries.size());
        LotteryInfoQueryResult lottery = upcomingLotteries.get(0);
        assertEquals(new LotteryInfoQueryResult(LOTTERY_ID, 0, LOTTERY_INFO), lottery);
    }

    @Ignore
    @Test
    public void findCustomers() {
        assertTrue(lotteryQueryService.findCustomers().isEmpty());

        customerCreatedEventHandler.handleMessage(new CustomerCreatedEvent(WhenCustomerIsCreated.CUSTOMER_ID, 0, WhenCustomerIsCreated.CUSTOMER_INFO));
        customerBalanceChangedEventHandler.handleMessage(new CustomerBalanceChangedEvent(WhenCustomerIsCreated.CUSTOMER_ID, 0, 0.0, 50.0, 50.0));

        List<CustomerAccountQueryResult> customers = lotteryQueryService.findCustomers();
        assertEquals(1, customers.size());
        CustomerAccountQueryResult customer = customers.get(0);
        assertEquals(WhenCustomerIsCreated.CUSTOMER_ID, customer.getCustomerId());
        assertEquals(0, customer.getCustomerVersion());
        assertEquals(WhenCustomerIsCreated.CUSTOMER_INFO.getName(), customer.getName());
        assertEquals(50.0, customer.getCurrentBalance(), 0.0);
    }
    
}
