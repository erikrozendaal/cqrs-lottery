package com.xebia.cqrs.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.events.Event;
import com.xebia.cqrs.events.Notification;
import com.xebia.cqrs.eventstore.EventSource;

public abstract class AggregateRoot<IdType> implements EventSource<Event> {

    private IdType id;
    private long version;
    private final List<Event> unsavedEvents;
    private final List<Notification> notifications;
    
    public AggregateRoot() {
        this.version = 0;
        this.unsavedEvents = new ArrayList<Event>();
        this.notifications = new ArrayList<Notification>();
    }
    
    protected abstract void onEvent(Event event);

    protected final void apply(Event event) {
        onEvent(event);
        unsavedEvents.add(event);
    }

    protected final void notify(Notification notification) {
        Validate.notNull(notification, "notification is required");
        notifications.add(notification);
    }
    
    public IdType getId() {
        return id;
    }

    protected void setId(IdType id) {
        this.id = id;
    }
    
    public long getVersion() {
        return version;
    }
    
    public void setVersion(long version) {
        Validate.isTrue(version > this.version, "version must always increase");
        this.version = version;
    }
    
    public void loadFromHistory(Iterable<? extends Event> events) {
        for (Event event : events) {
            onEvent(event);
        }
    }

    public List<? extends Event> getUnsavedEvents() {
        return new ArrayList<Event>(unsavedEvents);
    }
    
    public void clearUnsavedEvents() {
        unsavedEvents.clear();
    }

    public List<Notification> getNotifications() {
        return new ArrayList<Notification>(notifications);
    }
    
    public void clearNotifications() {
        notifications.clear();
    }

}
