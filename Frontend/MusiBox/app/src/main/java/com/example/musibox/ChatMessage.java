package com.example.musibox;

public class ChatMessage {
    private String content;
    private boolean sentByUser;
    private String timestamp; // Field for storing the timestamp

    // Constructor
    public ChatMessage(String content, boolean sentByUser, String timestamp) {
        this.content = content;
        this.sentByUser = sentByUser;
        this.timestamp = timestamp; // Use the actual timestamp passed in
    }

    // Getter for message content
    public String getContent() {
        return content;
    }

    // Getter to check if the message is sent by the user
    public boolean isSentByUser() {
        return sentByUser;
    }

    // Getter for the timestamp
    public String getTimestamp() {
        return timestamp;
    }
}
