package com.exchange;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class KrakenTest {

    private Kraken sut = new Kraken();

    @Test
    public void shouldParseUpdateMessageWithSingleBidValue() {
        Map<String, List<WssEntry>> result = sut.parseMessage("[320,{\"b\":[[\"50430.90000\",\"0.04165203\",\"1619362377.073850\"]],\"c\":\"2124612292\"},\"book-10\",\"XBT/USD\"]");

        assertNotNull(result);
        assertTrue(result.get(Exchange.ASKS_KEY).isEmpty());
        assertEquals(result.get(Exchange.BIDS_KEY).size(), 1);
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getPrice(), Double.valueOf("50430.90000"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getAmount(), Double.valueOf("0.04165203"));
    }

    @Test
    public void shouldParseUpdateMessageWithMultipleBidValues() {
        Map<String, List<WssEntry>> result = sut.parseMessage("[320,{\"b\":[[\"50430.90000\",\"0.04165203\",\"1619362377.073850\"],[\"50431.90000\",\"0.14165203\",\"1619362377.073850\",\"r\"]],\"c\":\"2124612292\"},\"book-10\",\"XBT/USD\"]");

        assertNotNull(result);
        assertTrue(result.get(Exchange.ASKS_KEY).isEmpty());
        assertEquals(result.get(Exchange.BIDS_KEY).size(), 2);
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getPrice(), Double.valueOf("50430.90000"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getAmount(), Double.valueOf("0.04165203"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(1).getPrice(), Double.valueOf("50431.90000"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(1).getAmount(), Double.valueOf("0.14165203"));
    }

    @Test
    public void shouldParseUpdateMessageWithSingleAskValue() {
        Map<String, List<WssEntry>> result = sut.parseMessage("[320,{\"a\":[[\"50434.00000\",\"0.00000000\",\"1619362377.468597\"],[\"50445.30000\",\"0.95287190\",\"1619362362.866297\",\"r\"]],\"c\":\"2124612292\"},\"book-10\",\"XBT/USD\"]");

        assertNotNull(result);
        assertTrue(result.get(Exchange.BIDS_KEY).isEmpty());
        assertEquals(result.get(Exchange.ASKS_KEY).size(), 2);
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getPrice(), Double.valueOf("50434.00000"));
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getAmount(), Double.valueOf("0.00000000"));
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(1).getPrice(), Double.valueOf("50445.30000"));
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(1).getAmount(), Double.valueOf("0.9528719"));
    }

    @Test
    public void shouldParseUpdateMessageWithMultipleAskValues() {
        Map<String, List<WssEntry>> result = sut.parseMessage("[320,{\"a\":[[\"50434.00000\",\"0.00000000\",\"1619362377.468597\"]],\"c\":\"2124612292\"},\"book-10\",\"XBT/USD\"]");

        assertNotNull(result);
        assertTrue(result.get(Exchange.BIDS_KEY).isEmpty());
        assertEquals(result.get(Exchange.ASKS_KEY).size(), 1);
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getPrice(), Double.valueOf("50434.00000"));
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getAmount(), Double.valueOf("0.00000000"));
    }

    @Test
    public void shouldParseUpdateMessageWithMultipleAsksAndBids() {
        Map<String, List<WssEntry>> result = sut.parseMessage("[320,{\"a\":[[\"50434.00000\",\"0.00000000\",\"1619362377.468597\"]],\"b\":[[\"50430.90000\",\"0.04165203\",\"1619362377.073850\"],[\"50431.90000\",\"0.14165203\",\"1619362377.073850\",\"r\"]],\"c\":\"2124612292\"},\"book-10\",\"XBT/USD\"]");

        assertNotNull(result);
        assertEquals(result.get(Exchange.ASKS_KEY).size(), 1);
        assertEquals(result.get(Exchange.BIDS_KEY).size(), 2);

        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getPrice(), Double.valueOf("50434.00000"));
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getAmount(), Double.valueOf("0.00000000"));

        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getPrice(), Double.valueOf("50430.90000"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getAmount(), Double.valueOf("0.04165203"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(1).getPrice(), Double.valueOf("50431.90000"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(1).getAmount(), Double.valueOf("0.14165203"));
    }

    @Test
    public void shouldParseSnapshotMessageWithMultipleAsksAndBids() {
        Map<String, List<WssEntry>> result = sut.parseMessage("[320,{\"as\":[[\"50431.00000\",\"6.68709690\",\"1619362375.090757\"],[\"50432.10000\",\"0.99188327\",\"1619362374.118744\"]],\n" +
                "\t  \"bs\":[[\"50430.90000\",\"0.08064203\",\"1619362373.574671\"],[\"50421.10000\",\"1.00000000\",\"1619362366.421125\"]]},\n" +
                "\t  \"book-10\",\"XBT/USD\"]");

        assertNotNull(result);
        assertEquals(result.get(Exchange.ASKS_KEY).size(), 2);
        assertEquals(result.get(Exchange.BIDS_KEY).size(), 2);

        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getPrice(), Double.valueOf("50431.00000"));
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(0).getAmount(), Double.valueOf("6.68709690"));
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(1).getPrice(), Double.valueOf("50432.10000"));
        assertEquals((Double) result.get(Exchange.ASKS_KEY).get(1).getAmount(), Double.valueOf("0.99188327"));

        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getPrice(), Double.valueOf("50430.90000"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(0).getAmount(), Double.valueOf("0.08064203"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(1).getPrice(), Double.valueOf("50421.10000"));
        assertEquals((Double) result.get(Exchange.BIDS_KEY).get(1).getAmount(), Double.valueOf("1.00000000"));
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
