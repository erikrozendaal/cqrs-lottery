package com.xebia.cqrs.eventstore.jdbc;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xebia.cqrs.eventstore.AbstractEventStoreTest;
import com.xebia.cqrs.eventstore.EventSerializer;
import com.xebia.cqrs.eventstore.EventStore;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-application-context.xml")
public class JdbcEventStoreTest extends AbstractEventStoreTest {
    
    @Autowired 
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private EventSerializer<String> eventSerializer = new EventSerializer<String>() {

        public Object serialize(String event) {
            return event;
        }

        public String deserialize(Object serialized) {
            return (String) serialized;
        }
    };

    @Override
    protected EventStore<String> createSubject() {
        assertNotNull("jdbcTemplate not injected", simpleJdbcTemplate);
        JdbcEventStore<String> result = new JdbcEventStore<String>(simpleJdbcTemplate, eventSerializer);
        result.init();
        return result;
    }
    
}
