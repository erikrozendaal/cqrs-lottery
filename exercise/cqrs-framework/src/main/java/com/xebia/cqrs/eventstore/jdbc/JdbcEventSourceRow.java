package com.xebia.cqrs.eventstore.jdbc;


public class JdbcEventSourceRow {

    private final Object id;
    private final Class<?> type;
    private long version;
    private long nextEventSequenceNumber;
    
    public JdbcEventSourceRow(Object id, Class<?> type, long version, long nextEventSequenceNumber) {
        this.id = id;
        this.type = type;
        this.version = version;
        this.nextEventSequenceNumber = nextEventSequenceNumber;
    }

    public Object getId() {
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

    public void updateNextEventSequenceNumber(long eventCount) {
        nextEventSequenceNumber += eventCount;
    }

}
