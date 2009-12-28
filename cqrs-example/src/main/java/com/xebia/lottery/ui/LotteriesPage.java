package com.xebia.lottery.ui;

import java.util.Date;

import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.xebia.cqrs.bus.Bus;
import com.xebia.cqrs.bus.Response;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.commands.CreateLotteryCommand;
import com.xebia.lottery.commands.DrawLotteryCommand;
import com.xebia.lottery.queries.LotteryInfoQueryResult;
import com.xebia.lottery.queries.LotteryQueryService;
import com.xebia.lottery.shared.LotteryInfo;

public class LotteriesPage extends AbstractLotteryPage {

    private static final long serialVersionUID = 1L;
    
    @SpringBean private LotteryQueryService lotteryQueryService;
    
    public LotteriesPage() {
        add(new ListView<LotteryInfoQueryResult>("lotteries", lotteryQueryService.findUpcomingLotteries()) {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            protected void populateItem(final ListItem<LotteryInfoQueryResult> item) {
                LotteryInfo info = item.getModelObject().getLotteryInfo();
                item.add(new Label("name", info.getName()));
                item.add(new Label("drawingTimestamp", formatDate(info.getDrawingTimestamp())));
                item.add(new Label("prizeAmount", String.valueOf(info.getPrizeAmount())));
                item.add(new Label("ticketPrice", String.valueOf(info.getTicketPrice())));
                item.add(new Link<Void>("drawLottery") {
                    private static final long serialVersionUID = 1L;
                    
                    @SpringBean private Bus bus;
                    
                    @Override
                    public void onClick() {
                        bus.sendAndWaitForResponse(new DrawLotteryCommand(item.getModelObject().getLotteryId()));
                    }
                    
                });
            }
        });
        add(new FeedbackPanel("feedback"));
        add(new CreateLotteryForm("createLotteryForm"));
    }
    
    private String formatDate(Date drawingTimestamp) {
        return new StyleDateConverter("MM", false).convertToString(drawingTimestamp, getLocale());
    }
    private static class CreateLotteryForm extends StatelessForm<CreateLotteryForm> {

        private static final long serialVersionUID = 1L;
        
        @SpringBean private Bus bus;
        
        private String name;
        private Date drawingTimestamp;
        private double prizeAmount;
        private double ticketPrice;

        public CreateLotteryForm(String id) {
            super(id);
            setDefaultModel(new CompoundPropertyModel<CreateLotteryForm>(this));
            add(new TextField<String>("name").setRequired(true));
            add(new DateTimeField("drawingTimestamp").setRequired(true));
            add(new TextField<Double>("prizeAmount").setRequired(true));
            add(new TextField<Double>("ticketPrice").setRequired(true));
        }
        
        @Override
        protected void onSubmit() {
            Response response = bus.sendAndWaitForResponse(new CreateLotteryCommand(VersionedId.random(), new LotteryInfo(name, drawingTimestamp, prizeAmount, ticketPrice)));
            System.err.println("response: " + response);
            setResponsePage(LotteriesPage.class);
        }

    }

}
