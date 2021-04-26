package com.exchange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.websocket.ClientEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ClientEndpoint
public class Kraken extends Exchange {
    static final String WSS_URL = "wss://ws.kraken.com";
    static final String KRAKEN_SUBSCRIBE_MSG = "{ \"event\": \"subscribe\", \"pair\": [\"BTC/USD\"], \"subscription\": {\"name\": \"book\"} }";

    public Kraken() {
        super(WSS_URL);
    }

    @Override
    public Map<String, List<WssEntry>> parseMessage(String message) {
        Map<String, List<WssEntry>> entriesMap = createEmptyEntriesMap();

        final JSONObject payloadObj = getPayload(message);
        if(payloadObj == null) {
            return entriesMap;
        }

        JSONArray asks = getEntries(payloadObj, "as", "a");
        if (asks != null) {
            entriesMap.get(ASKS_KEY).addAll(parseJsonArray(asks));
        }

        JSONArray bids = getEntries(payloadObj, "bs", "b");
        if (bids != null) {
            entriesMap.get(BIDS_KEY).addAll(parseJsonArray(bids));
        }

        return entriesMap;
    }

    private List<WssEntry> parseJsonArray(JSONArray entries) {
        List<WssEntry> result = new ArrayList<>();
        for (int i = 0; i < entries.length(); i++) {
            WssEntry entry = jsonToWssEntry(entries.getJSONArray(i));
            result.add(entry);
        }

        return result;
    }

    JSONArray getEntries(JSONObject payloadObj, String snapshotKey, String updateKey) {
        JSONArray snapshot = (JSONArray) payloadObj.opt(snapshotKey);
        if (snapshot == null) {
            return (JSONArray) payloadObj.opt(updateKey);
        }
        return snapshot;
    }

    private JSONObject getPayload(String str) {
        try {
            final JSONArray jsonArray = new JSONArray(new JSONTokener(str));
            return jsonArray.getJSONObject(1);
        } catch (JSONException je) {
            System.out.println("Unexpected message: " + str);
            return null;
        }
    }

    private WssEntry jsonToWssEntry(final JSONArray jsonArray) {
        double price = jsonArray.getDouble(0);
        double amount = jsonArray.getDouble(1);
        return new WssEntry(price, amount);
    }
}

