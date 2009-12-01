package com.xebia.cqrs.eventstore;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * Stores and tracks ordered streams of events.
 */
public interface EventStore<E> {

    /**
     * Creates a new event stream. The stream is initialized with the data and
     * events provided by source.
     * 
     * @param streamId
     *            the stream id of the stream to create.
     * @param source
     *            provides the type, initial version, initial timestamp, and
     *            initial events.
     * @throws DataIntegrityViolationException
     *             a stream with the specified id already exists.
     */
    void createEventStream(UUID streamId, EventSource<E> source) throws DataIntegrityViolationException;

    /**
     * Adds the events from source to the specified stream.
     * 
     * @param streamId
     *            the stream id.
     * @param expectedVersion
     *            the expected version of the stream.
     * @param source
     *            the stream data and events source.
     * @throws EmptyResultDataAccessException
     *             the specified stream does not exist.
     * @throws OptimisticLockingFailureException
     *             thrown when the expected version does not match the actual
     *             version of the stream.
     */
    void storeEventsIntoStream(UUID streamId, long expectedVersion, EventSource<E> source) throws EmptyResultDataAccessException,
            OptimisticLockingFailureException;

    /**
     * Loads the events associated with the stream into the provided sink.
     * 
     * @param streamId
     *            the stream id
     * @param sink
     *            the sink to send the stream data and events to.
     * @throws EmptyResultDataAccessException
     *             no stream with the specified id exists.
     */
    void loadEventsFromLatestStreamVersion(UUID streamId, EventSink<E> sink) throws EmptyResultDataAccessException;

    /**
     * Loads the events associated with the stream into the provided sink.
     * 
     * @param streamId
     *            the stream id
     * @param expectedVersion
     *            the expected version of the stream.
     * @param sink
     *            the sink to send the stream data and events to.
     * @throws EmptyResultDataAccessException
     *             no stream with the specified id exists.
     * @throws OptimisticLockingFailureException
     *             thrown when the expected version does not match the actual
     *             version of the stream.
     */
    void loadEventsFromSpecificStreamVersion(UUID streamId, long expectedVersion, EventSink<E> sink) throws EmptyResultDataAccessException,
            OptimisticLockingFailureException;

    /**
     * Loads the events associated with the stream into the provided sink. Only
     * the events that existed before and at the requested version are loaded.
     * 
     * @param streamId
     *            the stream id
     * @param version
     *            the version of the event stream to load.
     * @param sink
     *            the sink to send the stream data and events to.
     * @throws EmptyResultDataAccessException
     *             no stream with the specified id exists or the version is
     *             lower than the initial version of the stream.
     */
    void loadEventsFromStreamAtVersion(UUID streamId, long version, EventSink<E> sink) throws EmptyResultDataAccessException;

    /**
     * Loads the events associated with the stream into the provided sink. Only
     * the events that existed before and at the requested timestamp are loaded.
     * 
     * @param streamId
     *            the stream id
     * @param timestamp
     *            the timestamp of the event stream to load.
     * @param sink
     *            the sink to send the stream data and events to.
     * @throws EmptyResultDataAccessException
     *             no stream with the specified id exists or the version is
     *             lower than the initial version of the stream.
     */
    void loadEventsFromStreamAtTimestamp(UUID streamId, long timestamp, EventSink<E> sink);

}
