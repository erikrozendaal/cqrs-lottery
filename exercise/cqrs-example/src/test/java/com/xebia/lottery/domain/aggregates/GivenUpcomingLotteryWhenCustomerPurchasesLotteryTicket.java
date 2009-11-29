package com.xebia.lottery.domain.aggregates;

import static java.util.Arrays.*;

import java.util.Date;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import com.xebia.lottery.events.CustomerBalanceChangedEvent;
import com.xebia.lottery.events.CustomerCreatedEvent;
import com.xebia.lottery.events.LotteryCreatedEvent;
import com.xebia.lottery.events.LotteryTicketPurchasedEvent;
import com.xebia.lottery.shared.Address;
import com.xebia.lottery.shared.CustomerInfo;
import com.xebia.lottery.shared.LotteryInfo;


public class GivenUpcomingLotteryWhenCustomerPurchasesLotteryTicket extends BddTestCase {
    
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final CustomerInfo CUSTOMER_INFO = new CustomerInfo("Jan Jansen", "jan@jansen.nl", new Address("Plantage Middenlaan", "20", "1018 DE", "Amsterdam", "Nederland"));
    
    private static final UUID LOTTERY_ID = UUID.randomUUID();
    private static final LotteryInfo LOTTER_INFO = new LotteryInfo("lottery", new Date(System.currentTimeMillis() + 100000), 1000.00, 15);
    
    private Customer customer;
    private Lottery lottery;
    
    @Override
    protected void given() {
        customer = new Customer();
        customer.loadFromHistory(asList(
                new CustomerCreatedEvent(CUSTOMER_ID, 0, CUSTOMER_INFO), 
                new CustomerBalanceChangedEvent(CUSTOMER_ID, 0, 0, 50, 50)));
        
        lottery = new Lottery();
        lottery.loadFromHistory(asList(new LotteryCreatedEvent(LOTTERY_ID, 0, LOTTER_INFO)));
    }
    
    @Override
    protected void when() {
        lottery.purchaseTicketForCustomer(customer);
    }
    
    @Ignore
    @Test
    public void shouldRaiseTicketPurchasedEvent() {
        assertChange(
                lottery, 
                new LotteryTicketPurchasedEvent(LOTTERY_ID, 0, CUSTOMER_ID, "431130"));
    }
    
    @Ignore
    @Test
    public void shouldDeductTicketPriceFromCustomerBalance() {
        assertChange(
                customer,
                new CustomerBalanceChangedEvent(CUSTOMER_ID, 0, 50, -15, 35));
    }

}
