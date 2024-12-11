package com.example.musibox;

public class User {
    private String emailId;
    private boolean isActive;  // Field to track if user is active or banned

    // Constructor with email and active status passed as an argument
    public User(String emailId, boolean isActive) {
        this.emailId = emailId;
        this.isActive = true;  // Default status is active
    }

    public String getEmailId() {
        return emailId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}