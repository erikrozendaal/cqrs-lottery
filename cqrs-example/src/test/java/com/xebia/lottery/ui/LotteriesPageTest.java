package com.xebia.lottery.ui;

import static org.easymock.EasyMock.*;

import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import com.xebia.cqrs.bus.Response;
import com.xebia.lottery.commands.CreateLotteryCommand;

/**
 * Simple test using the WicketTester
 */
public class LotteriesPageTest extends LotteryPageTestCase {

    @Test
    public void testRenderMyPage() {
        expect(bus.sendAndWaitForResponse(isA(CreateLotteryCommand.class))).andReturn(new Response());
        replayMocks();
        
        // start and render the test page
        tester.startPage(LotteriesPage.class);

        // assert rendered page class
        tester.assertRenderedPage(LotteriesPage.class);

        FormTester form = tester.newFormTester("createLotteryForm");
        form.setValue("name", "lottery");
        form.setValue("drawingTimestamp:date", "12/12/09"); // US format :(
        form.setValue("drawingTimestamp:hours", "11");
        form.setValue("drawingTimestamp:minutes", "44");
        form.setValue("prizeAmount", "1000.00");
        form.setValue("ticketPrice", "1.50");
        form.submit();
        
        tester.assertNoErrorMessage();
        verifyMocks();
    }

}
