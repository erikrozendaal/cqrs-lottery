package com.xebia.cqrs.domain;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class AggregateRoot extends Entity<UUID> {

    public AggregateRoot(VersionedId versionedId) {
        super(new Aggregate(versionedId), versionedId.getId());
    }
    
    public void loadFromHistory(Iterable<? extends Event> events) {
        aggregate.loadFromHistory(events);
    }

    public Collection<? extends Object> getNotifications() {
        return aggregate.getNotifications();
    }

    public void clearNotifications() {
        aggregate.clearNotifications();
    }

    public List<? extends Event> getUnsavedEvents() {
        return aggregate.getUnsavedEvents();
    }

    public VersionedId getVersionedId() {
        return aggregate.getVersionedId();
    }

    public void clearUnsavedEvents() {
        aggregate.clearUnsavedEvents();
    }

    public void incrementVersion() {
        aggregate.incrementVersion();
    }

}
