package com.exchange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import javax.websocket.ClientEndpoint;
import java.util.List;
import java.util.Map;

@ClientEndpoint
public class Bitfinex extends Exchange {
    static final String WSS_URL = "wss://api-pub.bitfinex.com/ws/2";
    static final String BITFINEX_SUBSCRIBE_MSG = "{ \"event\": \"subscribe\", \"channel\": \"book\", \"symbol\": \"tBTCUSD\" }";

    public Bitfinex() {
        super(WSS_URL);
    }

    @Override
    public Map<String, List<WssEntry>> parseMessage(String message) {
        Map<String, List<WssEntry>> entriesMap = createEmptyEntriesMap();
        final JSONArray payload = getPayload(message);

        if (payload == null) {
            return entriesMap;
        }

        if (payload.get(0) instanceof JSONArray) {
            for (int i = 0; i < payload.length(); i++) {
                WssEntry entry = jsonToWssEntry(payload.getJSONArray(i));
                addEntry(entriesMap, entry);
            }
        } else {
            WssEntry entry = jsonToWssEntry(payload);
            addEntry(entriesMap, entry);
        }

        return entriesMap;
    }

    private void addEntry(Map<String, List<WssEntry>> result, WssEntry entry) {
        if (entry.getAmount() >= 0) {
            result.get(BIDS_KEY).add(entry);
        } else {
            entry.setAmount(0 - entry.getAmount());//asks are with negative amount, so negate
            result.get(ASKS_KEY).add(entry);
        }
    }

    private JSONArray getPayload(String str) {
        try {
            JSONArray jsonArray = new JSONArray(new JSONTokener(str));

            if (jsonArray.opt(1) == null) {
                return null;
            }

            return jsonArray.get(1) instanceof String ?
                    jsonArray.optJSONArray(2) : jsonArray.optJSONArray(1);
        } catch (JSONException je) {
            System.out.println("Unexpected message: " + str);
            return null;
        }
    }

    private WssEntry jsonToWssEntry(final JSONArray jsonArray) {
        double price = jsonArray.getDouble(0);
        double amount = jsonArray.getDouble(2);
        return new WssEntry(price, amount);
    }
}
