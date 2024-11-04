package com.example.androidexample;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageManager {
    private static ChatMessageManager instance;
    private final List<String> messages = new ArrayList<>();

    // Private constructor to prevent instantiation from other classes
    private ChatMessageManager() {}

    // Singleton pattern to ensure only one instance of ChatMessageManager exists
    public static synchronized ChatMessageManager getInstance() {
        if (instance == null) {
            instance = new ChatMessageManager();
        }
        return instance;
    }

    // Adds a message to the shared message list
    public void addMessage(String message) {
        messages.add(message);
    }

    // Retrieves all stored messages as a copy to prevent direct modification
    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    // Clears all stored messages, if needed
    public void clearMessages() {
        messages.clear();
    }
}