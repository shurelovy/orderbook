package com.exchange;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Orderbook {

    public static void main(String[] args) {
        new Orderbook().runOrderbook();
    }

    private void runOrderbook() {
        BlockingDeque<Map<String, List<WssEntry>>> queue = new LinkedBlockingDeque<>();
        subscribe(new Bitfinex(), Bitfinex.BITFINEX_SUBSCRIBE_MSG, queue);
        subscribe(new Kraken(), Kraken.KRAKEN_SUBSCRIBE_MSG, queue);

        while (true) {
            try {
                print(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void subscribe(Exchange exchange, String subscribeMsg,
                                  BlockingDeque<Map<String, List<WssEntry>>> queue) {
        exchange.connect();
        exchange.addMessageHandler(entry -> queue.add(entry));
        exchange.sendMessage(subscribeMsg);
    }

    private void print(Map<String, List<WssEntry>> map) {
        List<WssEntry> asks = map.get(Exchange.ASKS_KEY);
        printAsks(asks);

        List<WssEntry> bids = map.get(Exchange.BIDS_KEY);
        printBids(bids);
    }

    private void printBids(List<WssEntry> bids) {
        if (!bids.isEmpty()) {
            System.out.println("bids: " + bids);
            if (bids.size() > 1) {
                System.out.println("best bid: " + bids.get(0));
            }
        }
    }

    private void printAsks(List<WssEntry> asks) {
        if (!asks.isEmpty()) {
            System.out.println("asks: " + asks);
            if (asks.size() > 1) {
                System.out.println("best ask: " + asks.get(asks.size() - 1));
            }
        }
    }
}
