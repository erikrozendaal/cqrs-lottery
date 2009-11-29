package com.xebia.lottery.ui;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.xebia.cqrs.bus.Bus;
import com.xebia.cqrs.bus.Response;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.commands.CreateCustomerCommand;
import com.xebia.lottery.commands.ValidationError;
import com.xebia.lottery.queries.CustomerAccountQueryResult;
import com.xebia.lottery.queries.LotteryQueryService;
import com.xebia.lottery.shared.Address;
import com.xebia.lottery.shared.CustomerInfo;

public class CustomersPage extends AbstractLotteryPage {

    @SpringBean private LotteryQueryService lotteryQueryService;
    
    public CustomersPage() {
        add(new ListView<CustomerAccountQueryResult>("customers", lotteryQueryService.findCustomers()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<CustomerAccountQueryResult> item) {
                item.add(new Label("name", item.getModelObject().getCustomerName()));
                item.add(new Label("accountBalance", String.valueOf(item.getModelObject().getCurrentBalance())));
                item.add(new BookmarkablePageLink<CustomerTicketsPage>("tickets", CustomerTicketsPage.class, CustomerTicketsPage.link(item.getModelObject().getCustomerId().getId())));
            }
        });
        add(new FeedbackPanel("feedback"));
        add(new CreateCustomerForm("createCustomerForm"));
    }
    
    private static class CreateCustomerForm extends StatelessForm<CreateCustomerForm> {

        private static final long serialVersionUID = 1L;
        
        @SpringBean private Bus bus;
        
        private String name;
        private String email;
        private String streetName;
        private String houseNumber;
        private String postalCode;
        private String city;
        private double initialAccountBalance;
        
        public CreateCustomerForm(String id) {
            super(id);
            setDefaultModel(new CompoundPropertyModel<CreateCustomerForm>(this));
            add(new TextField<String>("name").setRequired(true));
            add(new TextField<String>("email").setRequired(true));
            add(new TextField<String>("streetName").setRequired(true));
            add(new TextField<String>("houseNumber").setRequired(true));
            add(new TextField<String>("postalCode").setRequired(true));
            add(new TextField<String>("city").setRequired(true));
            add(new TextField<Double>("initialAccountBalance").setRequired(true));
        }
        
        @Override
        protected void onSubmit() {
            Address address = new Address(streetName, houseNumber, postalCode, city, "Nederland");
            CustomerInfo info = new CustomerInfo(name, email, address);
            Response response = bus.sendAndWaitForResponse(new CreateCustomerCommand(VersionedId.random(), info, initialAccountBalance));
            System.err.println("response: " + response);
            for (ValidationError validationError : response.getRepliesOfType(ValidationError.class)) {
                error(validationError.getErrorMessage());
            }
            if (!hasError()) {
                setResponsePage(CustomersPage.class);
            }
        }

    }

}
