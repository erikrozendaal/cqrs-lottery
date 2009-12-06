package com.xebia.cqrs.eventstore;

import java.util.List;

/**
 * Source for change events. Every event source has its own sequence of events
 * that are stored by the {@link EventStore}. In DDD your aggregates are the
 * event sources.
 */
public interface EventSource<EventType> {

    String getType();

    long getVersion();
    
    long getTimestamp();

    List<? extends EventType> getEvents();

}
