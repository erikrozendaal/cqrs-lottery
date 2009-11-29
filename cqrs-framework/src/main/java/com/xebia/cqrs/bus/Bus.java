package com.xebia.cqrs.bus;

public interface Bus {

    void send(Object message) throws MessageHandlingException;
    
    /**
     * Sends a message to all registered handlers and returns the <em>first</em>
     * response.
     * 
     * @return the first response.
     * @exception MessageHandlingException
     *                an error occurred during message processing or no response
     *                was returned.
     */
    Response sendAndWaitForResponse(Object message) throws MessageHandlingException;

    /**
     * Reply to the sender of the current message with the specified message.
     */
    void reply(Object message) throws MessageHandlingException;

    /**
     * Reply to the sender of the current message with the specified messages.
     */
    void reply(Iterable<?> messages) throws MessageHandlingException;

    /**
     * Publishes a message to all subscribers.
     * 
     * @exception MessageHandlingException
     *                an error occurred during message processing.
     */
    void publish(Object message) throws MessageHandlingException;

    /**
     * Publishes the messages to all subscribers. The events should be treated
     * as a single unit-of-work.
     * 
     * @exception MessageHandlingException
     *                an error occurred during message processing.
     */
    void publish(Iterable<?> messages) throws MessageHandlingException;

    /**
     * The current message being processed.
     * 
     * @return null if no message is being processed.
     */
    Object getCurrentMessage();

}
