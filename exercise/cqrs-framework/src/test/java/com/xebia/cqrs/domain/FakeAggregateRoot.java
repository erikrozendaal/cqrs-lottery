package com.xebia.cqrs.domain;

import com.xebia.cqrs.events.Event;


public class FakeAggregateRoot extends AggregateRoot<String> {

    private String lastGreeting;

    public FakeAggregateRoot() {
        setId(AggregateRootTest.TEST_ID);
    }
    
    public void greetPerson(String name) {
        apply(new GreetingEvent(getId(), getVersion(), "Hi " + name));
        notify(new GreetingNotification("Greeted " + name));
    }
    
    public String getLastGreeting() {
        return lastGreeting;
    }
    
    @Override
    public void onEvent(Event event) {
        lastGreeting = ((GreetingEvent) event).getMessage();
    }

}