package com.xebia.cqrs.eventstore.jdbc;

import java.util.UUID;



public class JdbcEventSourceRow {

    private final UUID id;
    private final Class<?> type;
    private long version;
    private long nextEventSequenceNumber;
    
    public JdbcEventSourceRow(UUID id, Class<?> type) {
        this(id, type, 0, 0);
    }

    public JdbcEventSourceRow(UUID id, Class<?> type, long version, long nextEventSequenceNumber) {
        this.id = id;
        this.type = type;
        this.version = version;
        this.nextEventSequenceNumber = nextEventSequenceNumber;
    }

    public UUID getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    public long getVersion() {
        return version;
    }

    public long getNextEventSequenceNumber() {
        return nextEventSequenceNumber;
    }
    
    public long nextEventSequenceNumber() {
        return nextEventSequenceNumber++;
    }

}
