package com.xebia.lottery.ui;

import java.util.UUID;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.xebia.lottery.queries.CustomerTicketsQueryResult;
import com.xebia.lottery.queries.LotteryQueryService;

public class CustomerTicketsPage extends AbstractLotteryPage {

    @SpringBean private LotteryQueryService lotteryQueryService;
    
    public static PageParameters link(UUID customerId) {
        PageParameters parameters = new PageParameters();
        parameters.add("id", String.valueOf(customerId));
        return parameters;
    }
    
    public CustomerTicketsPage(PageParameters parameters) {
        UUID customerId = UUID.fromString(parameters.getString("id"));
        add(new Label("customerName", lotteryQueryService.getCustomerName(customerId)));
        add(new ListView<CustomerTicketsQueryResult>("tickets", lotteryQueryService.findLotteryTicketsForCustomer(customerId)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<CustomerTicketsQueryResult> item) {
                item.add(new Label("number", item.getModelObject().getTicketNumber()));
                item.add(new Label("lotteryName", item.getModelObject().getLotteryName()));
            }
        });
    }
    
}
