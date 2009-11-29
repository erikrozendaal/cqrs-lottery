package com.xebia.cqrs.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.xebia.cqrs.eventstore.EventSource;

public abstract class AggregateRoot implements EventSource<Event> {

    private VersionedId id;
    private final List<Event> unsavedEvents;
    private final List<Notification> notifications;
    
    public AggregateRoot(VersionedId id) {
        this.id = id;
        this.unsavedEvents = new ArrayList<Event>();
        this.notifications = new ArrayList<Notification>();
    }
    
    protected abstract void onEvent(Event event);

    protected void apply(Event event) {
        onEvent(event);
        unsavedEvents.add(event);
    }

    protected void notify(Notification notification) {
        Validate.notNull(notification, "notification is required");
        notifications.add(notification);
    }
    
    public VersionedId getVersionedId() {
        return id;
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
    
    public void incrementVersion() {
        id = id.nextVersion();
    }

    public List<Notification> getNotifications() {
        return new ArrayList<Notification>(notifications);
    }
    
    public void clearNotifications() {
        notifications.clear();
    }

}
