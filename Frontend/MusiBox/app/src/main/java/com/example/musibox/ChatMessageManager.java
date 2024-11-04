
package com.example.musibox;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageManager {
    private static ChatMessageManager instance;
    private final List<String> messages;

    private ChatMessageManager() {
        messages = new ArrayList<>();
    }

    public static ChatMessageManager getInstance() {
        if (instance == null) {
            instance = new ChatMessageManager();
        }
        return instance;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }
}