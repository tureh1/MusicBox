package onetomany.Friend;

import jakarta.persistence.*;
import onetomany.Users.User;

import java.util.Date;

@Entity
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Many-to-one relationship: many friends can belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    private Boolean isAccepted = false; // Whether the friendship has been accepted

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // =============================== Constructors ================================== //
    public Friend() {
        this.createdAt = new Date(); // Initialize creation date
    }

    public Friend(User user, User friend) {
        this.user = user;
        this.friend = friend;
        this.createdAt = new Date();
    }

    // =============================== Getters and Setters ================================== //

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
