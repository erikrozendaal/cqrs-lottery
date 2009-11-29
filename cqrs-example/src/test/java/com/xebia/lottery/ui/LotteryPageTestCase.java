package com.xebia.lottery.ui;

import static org.easymock.EasyMock.*;

import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

import com.xebia.cqrs.bus.Bus;
import com.xebia.lottery.queries.LotteryQueryService;

public abstract class LotteryPageTestCase {

    protected ApplicationContextMock context;
    protected WicketTester tester;
    protected Bus bus;
    protected LotteryQueryService lotteryQueryService;

    @Before
    public void setUpLotteryPageTestCase() {
        bus = createNiceMock(Bus.class);
        lotteryQueryService = createNiceMock(LotteryQueryService.class);
        
        context = new ApplicationContextMock();
        context.putBean("bus", bus);
        context.putBean("lotteryQueryService", lotteryQueryService);

        tester = new WicketTester(new WicketApplication(context));
    }
    
    protected void replayMocks() {
        replay(bus, lotteryQueryService);
    }
    
    protected void verifyMocks() {
        verify(bus, lotteryQueryService);
    }

}
