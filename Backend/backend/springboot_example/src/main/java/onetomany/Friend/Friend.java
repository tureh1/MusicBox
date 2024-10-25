package onetomany.Friend;

import jakarta.persistence.*;
import onetomany.Users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String friendName;
    private String friendEmail;

    private boolean isAccepted;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    public Friend() {
        // Default constructor
    }

    public Friend(String friendName, String friendEmail, User user) {
        this.friendName = friendName;
        this.friendEmail = friendEmail;
        this.user = user;
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
}
