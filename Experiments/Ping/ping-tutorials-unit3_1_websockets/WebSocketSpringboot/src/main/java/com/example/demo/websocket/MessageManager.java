package com.example.demo.websocket;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageManager {
    private static MessageManager instance;
    private final List<String> messages = new ArrayList<>();

    private MessageManager() {}

    public static synchronized MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
        }
        return instance;
    }

    // Adds a message to the shared message list
    public synchronized void addMessage(String message) {
        messages.add(message);
    }

    // Retrieves all stored messages
    public synchronized List<String> getMessages() {
        return new ArrayList<>(messages);
    }
}