package com.xebia.cqrs.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

public class Aggregate {

    private VersionedId versionedId;
    private final Map<Object, Entity<?>> entitiesById;
    private final List<Event> unsavedEvents;
    private final List<Notification> notifications;
    
    public Aggregate(VersionedId id) {
        this.versionedId = id;
        this.entitiesById = new HashMap<Object, Entity<?>>();
        this.unsavedEvents = new ArrayList<Event>();
        this.notifications = new ArrayList<Notification>();
    }
    
    protected void apply(Event event) {
        Entity<?> entity = entitiesById.get(event.getEntityId());
        entity.onEvent(event);
        unsavedEvents.add(event);
    }

    protected void notify(Notification notification) {
        Validate.notNull(notification, "notification is required");
        notifications.add(notification);
    }
    
    public void add(Entity<?> entity) {
        entitiesById.put(entity.getId(), entity);
    }
    
    public void remove(Entity<?> entity) {
        entitiesById.remove(entity.getId());
    }
    
    public VersionedId getVersionedId() {
        return versionedId;
    }
    
    public void loadFromHistory(Iterable<? extends Event> events) {
        for (Event event : events) {
            Entity<?> entity = entitiesById.get(event.getEntityId());
            entity.onEvent(event);
        }
    }

    public List<? extends Event> getUnsavedEvents() {
        return new ArrayList<Event>(unsavedEvents);
    }
    
    public void clearUnsavedEvents() {
        unsavedEvents.clear();
    }
    
    public void incrementVersion() {
        versionedId = versionedId.nextVersion();
    }

    public List<Notification> getNotifications() {
        return new ArrayList<Notification>(notifications);
    }
    
    public void clearNotifications() {
        notifications.clear();
    }
}
