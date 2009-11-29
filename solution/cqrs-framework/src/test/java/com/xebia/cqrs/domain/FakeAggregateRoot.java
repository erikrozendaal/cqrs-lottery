package com.xebia.cqrs.domain;



public class FakeAggregateRoot extends AggregateRoot {

    private String lastGreeting;

    public FakeAggregateRoot() {
        this(AggregateRootTest.TEST_ID);
    }
    
    public FakeAggregateRoot(VersionedId id) {
        super(id);
    }

    public void greetPerson(String name) {
        apply(new GreetingEvent(getVersionedId(), "Hi " + name));
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