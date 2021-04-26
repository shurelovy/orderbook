package com.exchange;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class BitfinexTest {

    private Bitfinex sut = new Bitfinex();

    @Test
    public void shouldParseMessageWithSingleBidValue() {
        Map<String, List<WssEntry>> result = sut.parseMessage("[23,[49602,1,0.151464]]");

        assertNotNull(result);
        assertTrue(result.get(Exchange.ASKS_KEY).isEmpty());
        assertEquals(result.get(Exchange.BIDS_KEY).size(), 1);
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getPrice(), Double.valueOf("49602"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getAmount(), Double.valueOf("0.151464"));
    }

    @Test
    public void shouldParseMessageWithSingleAskValue() {
        Map<String, List<WssEntry>> result = sut.parseMessage("[23,[49602,1,-0.151464]]");

        assertNotNull(result);
        assertTrue(result.get(Exchange.BIDS_KEY).isEmpty());
        assertEquals(result.get(Exchange.ASKS_KEY).size(), 1);
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getPrice(), Double.valueOf("49602"));
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getAmount(), Double.valueOf("0.151464"));
    }

    @Test
    public void shouldParseMessageWithMultipleAsksAndBids() {
        Map<String, List<WssEntry>> result = sut.parseMessage("[23,[[49612,1,0.151464],[49607,2,-0.201],[49605,1,0.403903]]]");

        assertNotNull(result);
        assertEquals(result.get(Exchange.ASKS_KEY).size(), 1);
        assertEquals(result.get(Exchange.BIDS_KEY).size(), 2);

        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getPrice(), Double.valueOf("49607"));
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getAmount(), Double.valueOf("0.201"));

        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getPrice(), Double.valueOf("49612"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getAmount(), Double.valueOf("0.151464"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(1).getPrice(), Double.valueOf("49605"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(1).getAmount(), Double.valueOf("0.403903"));
    }

    @Test
    public void shouldParseAnyUnexpectedMessageAsEmptyBidsAndAsks() {
        Map<String, List<WssEntry>> result = sut.parseMessage("[23,\"hb\"]");
        assertTrue(result.get(Exchange.ASKS_KEY).isEmpty());
        assertTrue(result.get(Exchange.BIDS_KEY).isEmpty());

        result = sut.parseMessage("[foo]");
        assertTrue(result.get(Exchange.ASKS_KEY).isEmpty());
        assertTrue(result.get(Exchange.BIDS_KEY).isEmpty());

        result = sut.parseMessage("[123, 456]");
        assertTrue(result.get(Exchange.ASKS_KEY).isEmpty());
        assertTrue(result.get(Exchange.BIDS_KEY).isEmpty());

        result = sut.parseMessage("[foo, boo");
        assertTrue(result.get(Exchange.ASKS_KEY).isEmpty());
        assertTrue(result.get(Exchange.BIDS_KEY).isEmpty());

        result = sut.parseMessage("foo, boo");
        assertTrue(result.get(Exchange.ASKS_KEY).isEmpty());
        assertTrue(result.get(Exchange.BIDS_KEY).isEmpty());
    }
}
