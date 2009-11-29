package com.xebia.cqrs.eventstore;


/**
 * Responsible for (de-)serializing events.
 */
public interface EventSerializer<E> {

    Object serialize(E event);
    
    E deserialize(Object serialized);
    
}
