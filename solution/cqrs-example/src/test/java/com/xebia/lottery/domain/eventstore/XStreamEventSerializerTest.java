package com.xebia.lottery.domain.eventstore;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.domain.aggregates.WhenCustomerIsCreated;
import com.xebia.lottery.domain.aggregates.WhenLotteryIsCreated;
import com.xebia.lottery.events.LotteryTicketPurchasedEvent;


public class XStreamEventSerializerTest {

    private static final VersionedId LOTTERY_ID = WhenLotteryIsCreated.LOTTERY_ID;
    private static final VersionedId CUSTOMER_ID = WhenCustomerIsCreated.CUSTOMER_ID;

    private static final LotteryTicketPurchasedEvent EVENT = new LotteryTicketPurchasedEvent(LOTTERY_ID, CUSTOMER_ID, "7122");
    private static final String SERIALIZED_EVENT = "<event.LotteryTicketPurchasedEvent>\n" +
                    "  <aggregateRootId>\n" +
                    "    <id>" + LOTTERY_ID.getId() + "</id>\n" +
                    "    <version>" + LOTTERY_ID.getVersion() + "</version>\n" +
                    "  </aggregateRootId>\n" +
                    "  <customerId>\n" +
                    "    <id>" + CUSTOMER_ID.getId() + "</id>\n" +
                    "    <version>" + CUSTOMER_ID.getVersion() + "</version>\n" +
                    "  </customerId>\n" +
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
