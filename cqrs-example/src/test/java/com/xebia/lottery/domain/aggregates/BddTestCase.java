package com.xebia.lottery.domain.aggregates;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;

import com.xebia.cqrs.domain.AggregateRoot;
import com.xebia.cqrs.domain.Event;

public abstract class BddTestCase {
    protected Exception caught = null;
    
    protected void given() {}
    protected abstract void when();
    
    @Before
    public void setup() {
        given();
        when();
    }
    
    protected static void assertChange(AggregateRoot aggregateRoot, Event expected) {
        List<? extends Event> changes = aggregateRoot.getUnsavedEvents();
        Event matchedType = null;
        for (Event change : changes) {
            if (change.equals(expected)) {
                return;
            } else if (change.getClass() == expected.getClass()) {
                matchedType = change;
            }
        }
        if (matchedType == null) {
            fail("event <" + expected + "> not found in changes " + changes);
        } else {
            fail("event <" + expected + "> not found, but event of matching type was <" + matchedType + ">");
        }
    }
}
