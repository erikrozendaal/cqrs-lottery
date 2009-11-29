package com.xebia.cqrs.domain;

import java.util.UUID;

import org.apache.commons.lang.Validate;

public class VersionedId extends ValueObject {

    private static final long serialVersionUID = 1L;

    public static final long INITIAL_VERSION = 0;
    private static final long LATEST_VERSION = Long.MAX_VALUE;
    
    private final UUID id;
    private final long version;
    
    private VersionedId(UUID id, long version) {
        Validate.notNull(id, "id is required");
        Validate.isTrue(version >= INITIAL_VERSION, "version must be greater than or equal to INITIAL_VERSION");
        this.id = id;
        this.version = version;
    }
    
    public static VersionedId random() {
        return forInitialVersion(UUID.randomUUID());
    }
    
    public static VersionedId forInitialVersion(UUID id) {
        return forSpecificVersion(id, INITIAL_VERSION);
    }

    public static  VersionedId forLatestVersion(UUID id) {
        return forSpecificVersion(id, LATEST_VERSION);
    }

    public static VersionedId forSpecificVersion(UUID id, long version) {
        return new VersionedId(id, version);
    }

    public UUID getId() {
        return id;
    }
    
    public long getVersion() {
        return version;
    }
    
    public boolean isForInitialVersion() {
        return version == INITIAL_VERSION;
    }

    public boolean isForLatestVersion() {
        return version == LATEST_VERSION;
    }
    
    public boolean isForSpecificVersion() {
        return !isForLatestVersion();
    }
    
    public VersionedId withVersion(long version) {
        return VersionedId.forSpecificVersion(id, version);
    }

    public VersionedId nextVersion() {
        if (isForLatestVersion()) {
            return this; 
        } else {
            return withVersion(version + 1);
        }
    }
    
    public boolean equalsIgnoreVersion(VersionedId other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return id.equals(other.id);
    }
    
    public boolean isCompatible(VersionedId other) {
        Validate.isTrue(other.isForSpecificVersion(), "cannot check for compatibility with non-specific version");
        if (isForLatestVersion()) {
            return equalsIgnoreVersion(other);
        } else {
            return equals(other);
        }
    }

    @Override
    public String toString() {
        if (isForLatestVersion()) {
            return id.toString();
        } else {
            return id + "#" + version;
        }
    }

}
