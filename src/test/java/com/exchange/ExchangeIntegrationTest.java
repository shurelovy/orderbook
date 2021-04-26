package com.exchange;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class ExchangeIntegrationTest {

    @Test
    public void shouldReceiveBitfinexMessages() throws InterruptedException {
        testExchange(new Bitfinex(), Bitfinex.BITFINEX_SUBSCRIBE_MSG);
    }

    @Test
    public void shouldReceiveKrakenMessages() throws InterruptedException {
        testExchange(new Kraken(), Kraken.KRAKEN_SUBSCRIBE_MSG);
    }

    void testExchange(Exchange exchange, String subscribeMessage) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        exchange.connect();
        exchange.addMessageHandler(map -> {
            assertTrue(!map.get(Exchange.ASKS_KEY).isEmpty() ||
                                !map.get(Exchange.BIDS_KEY).isEmpty());
            latch.countDown();
        });
        exchange.sendMessage(subscribeMessage);
        latch.await(10, TimeUnit.SECONDS);
        if(latch.getCount() != 0) {
            Assert.fail("All expected messages were not received");
        }
    }

}
