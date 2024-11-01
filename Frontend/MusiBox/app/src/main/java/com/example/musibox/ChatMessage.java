package com.example.musibox;

public class    ChatMessage {
    private String content;
    private boolean sentByUser;
    private String timestamp; // New field for timestamp

    public ChatMessage(String content, boolean sentByUser, String timestamp) {
        this.content = content;
        this.sentByUser = sentByUser;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public boolean isSentByUser() {
        return sentByUser;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
