package com.example.musibox;

public class ChatMessage {
    private String text;   // The content of the message
    private boolean sentByUser; // Indicates if the message was sent by the user

    // Constructor
    public ChatMessage(String text, boolean sentByUser) {
        this.text = text;
        this.sentByUser = sentByUser;
    }

    // Getter for message text
    public String getText() {
        return text;
    }

    // Method to check if the message was sent by the user
    public boolean isSentByUser() {
        return sentByUser;
    }
}