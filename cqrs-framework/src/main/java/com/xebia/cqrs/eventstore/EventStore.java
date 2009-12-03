package com.xebia.cqrs.eventstore;

import java.util.UUID;

import org.springframework.dao.ConcurrencyFailureException;

import com.xebia.cqrs.domain.VersionedId;

/**
 * Stores and loads events for {@link EventSource}s.
 */
public interface EventStore<E> {

    /**
     * Saves the changes for the specified {@link EventSource}.
     */
    void storeEventSource(EventSource<? extends E> source) throws ConcurrencyFailureException;

    /**
     * Loads all change events for the event source with the specified 
     * <code>eventSourceId</code>.
     * 
     * @return null if no event source of the specified eventSourceId was found.
     */
    <T extends EventSource<? super E>> T loadEventSource(Class<T> expectedType, UUID eventSourceId);

    /**
     * Loads all change events for the event source with the specified versioned
     * <code>eventSourceId</code>.
     * 
     * @return null if no event source of the specified eventSourceId was found.
     */
    <T extends EventSource<? super E>> T loadEventSource(Class<T> expectedType, VersionedId eventSourceId);

}
