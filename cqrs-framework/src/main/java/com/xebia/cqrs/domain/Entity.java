package com.xebia.cqrs.domain;

public abstract class Entity<IdType> {

    private final IdType id;
    protected final Aggregate aggregate;
    
    public Entity(Aggregate aggregateContext, IdType id) {
        this.aggregate = aggregateContext;
        this.id = id;
        this.aggregate.add(this);
    }

    public IdType getId() {
        return id;
    }
    
    protected void apply(Event event) {
        aggregate.apply(event);
    }
    
    protected void notify(Notification notification) {
        aggregate.notify(notification);
    }

    protected abstract void onEvent(Event event);
    
}
