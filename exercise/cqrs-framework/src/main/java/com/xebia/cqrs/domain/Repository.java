package com.xebia.cqrs.domain;


public interface Repository {

    <IdType, T extends AggregateRoot<IdType>> T get(Class<T> type, IdType id, long version);
    
    <T extends AggregateRoot<?>> void save(T aggregate);
    
}
