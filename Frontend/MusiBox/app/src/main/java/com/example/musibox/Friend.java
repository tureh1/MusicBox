package com.example.musibox;

public class Friend {
    private String friendName;
    private String friendEmail;

    // Constructor
    public Friend(String friendName, String friendEmail) {
        this.friendName = friendName;
        this.friendEmail = friendEmail;
    }

    // Getters
    public String getFriendName() {
        return friendName;
    }

    public String getFriendEmail() {
        return friendEmail;
    }
}