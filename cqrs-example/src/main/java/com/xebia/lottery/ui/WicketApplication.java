package com.xebia.lottery.ui;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see com.xebia.lottery.ui.Start#main(String[])
 */
@Component
public class WicketApplication extends WebApplication
{    
    
    private ApplicationContext applicationContext;

    /**
     * Constructor
     */
	public WicketApplication()
	{
	    this(null);
	}
	
	public WicketApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
	protected void init() {
	    super.init();
	    
	    initBookmarkablePages();
        initSpringInjection();
	}

    private void initBookmarkablePages() {
        mountBookmarkablePage("customers", CustomersPage.class);
        mountBookmarkablePage("lotteries", LotteriesPage.class);
        mountBookmarkablePage("purchase-ticket", PurchaseTicketPage.class);
    }

    private void initSpringInjection() {
        if (applicationContext == null) {
	        addComponentInstantiationListener(new SpringComponentInjector(this));
	    } else {
            addComponentInstantiationListener(new SpringComponentInjector(this, applicationContext));
	    }
    }
	
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<PurchaseTicketPage> getHomePage()
	{
		return PurchaseTicketPage.class;
	}

}
