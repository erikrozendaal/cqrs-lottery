package com.xebia.lottery.domain.aggregates;

import static java.util.Arrays.*;

import org.junit.Test;

import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.events.CustomerBalanceChangedEvent;
import com.xebia.lottery.events.CustomerCreatedEvent;
import com.xebia.lottery.events.LotteryCreatedEvent;
import com.xebia.lottery.events.LotteryTicketPurchasedEvent;
import com.xebia.lottery.shared.Address;
import com.xebia.lottery.shared.CustomerInfo;
import com.xebia.lottery.shared.LotteryInfo;


public class GivenUpcomingLotteryWhenCustomerPurchasesLotteryTicket extends BddTestCase {
    
    private static final VersionedId CUSTOMER_ID = WhenCustomerIsCreated.CUSTOMER_ID;
    private static final CustomerInfo CUSTOMER_INFO = new CustomerInfo("Jan Jansen", "jan@jansen.nl", new Address("Plantage Middenlaan", "20", "1018 DE", "Amsterdam", "Nederland"));
    
    private static final VersionedId LOTTERY_ID = WhenLotteryIsCreated.LOTTERY_ID;
    private static final LotteryInfo LOTTER_INFO = WhenLotteryIsCreated.LOTTER_INFO;
    
    private Customer customer;
    private Lottery lottery;
    
    @Override
    protected void given() {
        customer = new Customer(CUSTOMER_ID);
        customer.loadFromHistory(asList(
                new CustomerCreatedEvent(CUSTOMER_ID, CUSTOMER_INFO), 
                new CustomerBalanceChangedEvent(CUSTOMER_ID, 0, 50, 50)));
        
        lottery = new Lottery(LOTTERY_ID);
        lottery.loadFromHistory(asList(new LotteryCreatedEvent(LOTTERY_ID, LOTTER_INFO)));
    }
    
    @Override
    protected void when() {
        lottery.purchaseTicketForCustomer(customer);
    }
    
    @Test
    public void shouldRaiseTicketPurchasedEvent() {
        assertChange(
                lottery, 
                new LotteryTicketPurchasedEvent(LOTTERY_ID, CUSTOMER_ID, "431130"));
    }
    
    @Test
    public void shouldDeductTicketPriceFromCustomerBalance() {
        assertChange(
                customer,
                new CustomerBalanceChangedEvent(CUSTOMER_ID, 50, -15, 35));
    }

}
