package com.xebia.cqrs.domain;

import java.util.UUID;


public interface Repository {

    <T extends AggregateRoot> T getById(Class<T> type, UUID id);
    
    < T extends AggregateRoot> T getByVersionedId(Class<T> type, VersionedId id);
    
    <T extends AggregateRoot> void add(T aggregate);
    
}
