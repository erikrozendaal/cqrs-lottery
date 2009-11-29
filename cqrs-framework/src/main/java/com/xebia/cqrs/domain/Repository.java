package com.xebia.cqrs.domain;


public interface Repository {

    < T extends AggregateRoot> T get(Class<T> type, VersionedId id);
    
    <T extends AggregateRoot> void add(T aggregate);
    
}
