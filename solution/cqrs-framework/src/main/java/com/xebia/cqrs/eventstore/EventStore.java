package com.xebia.cqrs.eventstore;

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
     * @param version the version of the last applied event(s).
     * @return null if no event source of the specified eventSourceId was found.
     */
    <T extends EventSource<? super E>> T loadEventSource(Class<T> expectedType, VersionedId eventSourceId);

}
