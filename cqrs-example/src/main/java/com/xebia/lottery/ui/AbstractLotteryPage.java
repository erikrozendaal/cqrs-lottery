package com.xebia.lottery.ui;

import org.apache.wicket.IPageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

public abstract class AbstractLotteryPage extends WebPage {

    private static final long serialVersionUID = 1L;

    public AbstractLotteryPage() {
        super();
    }

    public AbstractLotteryPage(IModel<?> model) {
        super(model);
    }

    public AbstractLotteryPage(IPageMap pageMap, IModel<?> model) {
        super(pageMap, model);
    }

    public AbstractLotteryPage(IPageMap pageMap, PageParameters parameters) {
        super(pageMap, parameters);
    }

    public AbstractLotteryPage(IPageMap pageMap) {
        super(pageMap);
    }

    public AbstractLotteryPage(PageParameters parameters) {
        super(parameters);
    }
    
}
