package com.exchange;

import javax.websocket.*;
import java.net.URI;
import java.util.*;

public abstract class Exchange {
    public static final String ASKS_KEY = "asks";
    public static final String BIDS_KEY = "bids";

    private String url;
    private Session userSession;
    private MessageHandler messageHandler;

    public Exchange(String url) {
        this.url = url;
    }

    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
        System.out.println("Opened connection to " + url);
    }

    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null && message.startsWith("[")) {
            Map<String, List<WssEntry>> entriesMap = parseMessage(message);
            Collections.sort(entriesMap.get(BIDS_KEY));
            Collections.sort(entriesMap.get(ASKS_KEY));

            this.messageHandler.handleMessage(entriesMap);
        }
    }

    public abstract Map<String, List<WssEntry>> parseMessage(String message);

    protected Map<String, List<WssEntry>> createEmptyEntriesMap() {
        Map<String, List<WssEntry>> entriesMap = new HashMap<>();
        entriesMap.put(ASKS_KEY, new ArrayList<>());
        entriesMap.put(BIDS_KEY, new ArrayList<>());
        return entriesMap;
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public interface MessageHandler {
        void handleMessage(Map<String, List<WssEntry>> entries);
    }
}
