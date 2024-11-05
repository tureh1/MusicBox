package com.example.musibox;

public class Message {
    private String username;
    private String messageContent;
    private String friendEmail;
    public Message(String username, String messageContent) {
        this.username = username;
        this.messageContent = messageContent;
        this.friendEmail = friendEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getFriendEmail() {
        return friendEmail;
    }


}
