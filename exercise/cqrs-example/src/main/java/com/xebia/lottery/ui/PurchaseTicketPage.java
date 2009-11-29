package com.xebia.lottery.ui;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.xebia.cqrs.bus.Bus;
import com.xebia.cqrs.bus.Response;
import com.xebia.lottery.commands.PurchaseLotteryTicketCommand;
import com.xebia.lottery.commands.ValidationError;
import com.xebia.lottery.queries.CustomerAccountQueryResult;
import com.xebia.lottery.queries.LotteryInfoQueryResult;
import com.xebia.lottery.queries.LotteryQueryService;

public class PurchaseTicketPage extends AbstractLotteryPage {

    public PurchaseTicketPage() {
        add(new FeedbackPanel("feedback"));
        add(new PurchaseTicketForm("purchaseTicketForm"));
    }
    
    private static class PurchaseTicketForm extends StatelessForm<PurchaseTicketForm> {

        private static final long serialVersionUID = 1L;
        
        @SpringBean private Bus bus;
        @SpringBean private LotteryQueryService lotteryQueryService;

        private LotteryInfoQueryResult selectedLottery;
        private CustomerAccountQueryResult selectedCustomer;
        
        public PurchaseTicketForm(String id) {
            super(id);
            setDefaultModel(new CompoundPropertyModel<PurchaseTicketForm>(this));
            add(new DropDownChoice<LotteryInfoQueryResult>(
                    "selectedLottery", 
                    lotteryQueryService.findUpcomingLotteries(), 
                    new ChoiceRenderer<LotteryInfoQueryResult>("info.name")).setNullValid(false).setRequired(true));
            add(new DropDownChoice<CustomerAccountQueryResult>(
                    "selectedCustomer", 
                    lotteryQueryService.findCustomers(), 
                    new ChoiceRenderer<CustomerAccountQueryResult>("name")).setNullValid(false).setRequired(true));
        }
        
        @Override
        protected void onSubmit() {
            Response response = bus.sendAndWaitForResponse(new PurchaseLotteryTicketCommand(selectedLottery.getId(), selectedLottery.getVersion(), selectedCustomer.getCustomerId(), selectedCustomer.getCustomerVersion()));
            for (ValidationError validationError : response.getNotificationsOfType(ValidationError.class)) {
                error(validationError.getErrorMessage());
            }
            if (!hasError()) {
                setResponsePage(CustomerTicketsPage.class, CustomerTicketsPage.link(selectedCustomer.getCustomerId()));
            }
        }

    }

}
