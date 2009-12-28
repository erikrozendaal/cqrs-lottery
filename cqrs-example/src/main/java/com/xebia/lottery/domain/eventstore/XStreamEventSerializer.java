package com.xebia.lottery.domain.eventstore;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;
import com.xebia.cqrs.domain.Event;
import com.xebia.cqrs.eventstore.EventSerializer;
import com.xebia.lottery.events.LotteryEvent;

@Component
public class XStreamEventSerializer implements EventSerializer<Event> {
    
    private static final Logger LOG = Logger.getLogger(XStreamEventSerializer.class);

    private XStream xstream;
    
    public XStreamEventSerializer() {
        xstream = new XStream();
        xstream.aliasPackage("event", LotteryEvent.class.getPackage().getName());
        xstream.addImmutableType(UUID.class);
    }
    
    public Event deserialize(Object serialized) {
        Event result = (Event) xstream.fromXML((String) serialized);
        if (LOG.isDebugEnabled()) {
            LOG.debug("deserialized " + result + " from " + serialized);
        }
        return result;
    }

    public Object serialize(Event event) {
        String result = xstream.toXML(event);
        if (LOG.isDebugEnabled()) {
            LOG.debug("serialized " + event + " to " + result);
        }
        return result;
    }

}
