package com.xebia.cqrs.eventstore;


/**
 * Source for change events. Every event source has its own sequence of events
 * that are stored by the {@link EventStore}. In DDD your aggregates are the
 * event sources.
 */
public interface EventSink<EventType> {

    void setType(Class<?> type);
    
    void setVersion(long version);
    
    void setTimestamp(long timestamp);
    
    void setEvents(Iterable<? extends EventType> events);

}
