package com.xebia.cqrs.eventstore;

import java.util.List;

import com.xebia.cqrs.domain.VersionedId;

/**
 * Source for change events. Every event source has its own sequence of events
 * that are stored by the {@link EventStore}. In DDD your aggregates are the
 * event sources.
 */
public interface EventSource<T> {

    /**
     * The (globally) unique id of your aggregate. If your aggregate does not
     * have a globally unique id, you can consider using
     * "aggregate-root-type#aggregate-root-id" to ensure uniqueness.
     */
    VersionedId getVersionedId();

    /**
     * Loads the history for this {@link EventSource}.
     */
    void loadFromHistory(Iterable<? extends T> history);
    
    /**
     * The unsaved events for this event source.
     */
    List<? extends T> getUnsavedEvents();

}
