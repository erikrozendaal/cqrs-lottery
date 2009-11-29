package com.xebia.cqrs.domain;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;


public class VersionedIdTest {
    
    private final static UUID FOO = UUID.randomUUID();
    private final static UUID BAR = UUID.randomUUID();
    
    private VersionedId a = VersionedId.forSpecificVersion(FOO, 3);
    
    @Test
    public void shouldStoreIdAndVersion() {
        assertEquals(FOO, a.getId());
        assertEquals(3, a.getVersion());
    }
    
    @Test
    public void testEqualsIgnoreVersion() {
        assertTrue(a.equalsIgnoreVersion(VersionedId.forSpecificVersion(FOO, 1)));
        assertFalse(a.equalsIgnoreVersion(null));
        assertFalse(a.equalsIgnoreVersion(VersionedId.forSpecificVersion(BAR, 3)));
    }
    
    @Test
    public void testCompatibility() {
        assertTrue(VersionedId.forLatestVersion(FOO).isCompatible(a));
        assertFalse(VersionedId.forLatestVersion(BAR).isCompatible(a));
        assertTrue(a.isCompatible(VersionedId.forSpecificVersion(FOO, 3)));
        assertFalse(a.isCompatible(VersionedId.forSpecificVersion(FOO, 2)));
        assertFalse(a.isCompatible(VersionedId.forSpecificVersion(BAR, 3)));
    }

}
