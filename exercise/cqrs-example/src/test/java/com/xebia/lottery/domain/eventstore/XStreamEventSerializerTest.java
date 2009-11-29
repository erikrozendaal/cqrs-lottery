package com.xebia.lottery.domain.eventstore;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import com.xebia.lottery.domain.eventstore.XStreamEventSerializer;
import com.xebia.lottery.events.LotteryTicketPurchasedEvent;


public class XStreamEventSerializerTest {

    private static final UUID LOTTERY_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();

    private static final LotteryTicketPurchasedEvent EVENT = new LotteryTicketPurchasedEvent(LOTTERY_ID, 1, CUSTOMER_ID, "7122");
    private static final String SERIALIZED_EVENT = "<event.LotteryTicketPurchasedEvent>\n" +
                    "  <aggregateRootId class=\"uuid\">" + LOTTERY_ID + "</aggregateRootId>\n" +
                    "  <aggregateRootVersion>1</aggregateRootVersion>\n" +
                    "  <customerId>" + CUSTOMER_ID +"</customerId>\n" +
                    "  <ticketNumber>7122</ticketNumber>\n" +
                    "</event.LotteryTicketPurchasedEvent>";
    
    private XStreamEventSerializer subject = new XStreamEventSerializer();
    
    @Test
    public void shouldSerializeEvent() {
        assertEquals(SERIALIZED_EVENT, subject.serialize(EVENT));
    }

    @Test
    public void shouldDeserializeEvent() {
        assertEquals(EVENT, subject.deserialize(SERIALIZED_EVENT));
    }

}
