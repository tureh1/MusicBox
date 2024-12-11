package onetomany.Profile;

import jakarta.persistence.*;
import onetomany.Users.User;

@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    private String bio;

    private int backgroundColor = 0xFF000000; // Default color is black (ARGB hex format)

    // =============================== Constructors ================================== //
    public Profile() {
        // Default constructor
    }

    public Profile(User user, String bio) {
        this.user = user;
        this.bio = bio;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}