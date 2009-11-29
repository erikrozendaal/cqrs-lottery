package com.xebia.lottery;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xebia.cqrs.bus.Bus;
import com.xebia.lottery.commands.CreateCustomerCommand;
import com.xebia.lottery.commands.CreateLotteryCommand;
import com.xebia.lottery.domain.aggregates.WhenCustomerIsCreated;
import com.xebia.lottery.domain.aggregates.WhenLotteryIsCreated;
import com.xebia.lottery.shared.CustomerInfo;
import com.xebia.lottery.shared.LotteryInfo;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-application-context.xml")
public class ExercisesTest {
    
    @Autowired private Bus bus;
    
    @Test
    public void testCreateLottery() {
        LotteryInfo lotteryInfo = WhenLotteryIsCreated.LOTTER_INFO;
        CreateLotteryCommand command = new CreateLotteryCommand(lotteryInfo);
        
        bus.sendAndWaitForResponse(command);
    }
    
    @Ignore
    @Test
    public void testCreateCustomer() {
        CustomerInfo customerInfo = WhenCustomerIsCreated.CUSTOMER_INFO;
        CreateCustomerCommand command = new CreateCustomerCommand(customerInfo, 100.0);
        
        bus.sendAndWaitForResponse(command);
    }

}
