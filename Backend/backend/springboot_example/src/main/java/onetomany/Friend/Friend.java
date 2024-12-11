package onetomany.Friend;

import jakarta.persistence.*;
import onetomany.Users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private boolean isAccepted;
    private String friendName;
    private String friendEmail;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Add cascade here
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    public Friend() {
        // Default constructor
    }

    public Friend(User user, String friendEmail, boolean isAccepted) {
        this.user = user; // Make sure this user is properly assigned
        this.friendEmail = friendEmail;
        this.isAccepted = isAccepted;
        this.friendName = null; // Initialize friendName if needed, set later
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAccepted() { // Method name updated to follow Java naming conventions
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) { // Method name updated to follow Java naming conventions
        this.isAccepted = isAccepted;
    }
}
