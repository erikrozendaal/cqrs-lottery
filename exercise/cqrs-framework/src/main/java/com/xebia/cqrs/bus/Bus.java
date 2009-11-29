package com.xebia.cqrs.bus;

import java.util.Collection;

import com.xebia.cqrs.commands.Command;
import com.xebia.cqrs.events.Event;
import com.xebia.cqrs.events.Notification;

public interface Bus {

    /**
     * Sends a command to all registered handlers.
     * 
     * @return the response containing the (possibly empty) collection of all replied notifications.
     * @exception MessageHandlingException an error occurred during message processing. 
     */
    Response sendAndWaitForResponse(Command command) throws MessageHandlingException;

    /**
     * Reply to the current command with the specified notification.
     */
    void reply(Notification notification) throws MessageHandlingException;
    
    /**
     * Reply to the current command with the specified notifications.
     */
    void reply(Collection<? extends Notification> notifications) throws MessageHandlingException;
    
    /**
     * Publishes an event to all subscribers.
     * @exception MessageHandlingException an error occurred during message processing. 
     */
    void publish(Event event) throws MessageHandlingException;

    /**
     * Publishes all events to all subscribers. The events should be treated as
     * a single unit-of-work.
     * @exception MessageHandlingException an error occurred during message processing. 
     */
    void publish(Collection<? extends Event> events) throws MessageHandlingException;

    /**
     * The current message being processed.
     * 
     * @return null if no message is being processed.
     */
    Object getCurrentMessage();
    
}
