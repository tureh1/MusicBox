package onetomany.Users;

import jakarta.persistence.*;
import java.util.List;
import onetomany.Friend.Friend;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String emailId;
    private String password;
    private Boolean ifActive = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Friend> friends;

    // =============================== Constructors ================================== //

    public User(String emailId, String password) {
        this.emailId = emailId;
        this.password = password; // Initialize password
    }

    public User() {
        // Default constructor
    }

    // =============================== Getters and Setters for each field ================================== //

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() { // Getter for password
        return password;
    }

    public void setPassword(String password) { // Setter for password
        this.password = password;
    }

    public Boolean getIfActive() {
        return ifActive;
    }

    public void setIfActive(Boolean ifActive) {
        this.ifActive = ifActive;
    }

    // Getter and Setter for friends list
    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    // ==================== Static class for updating password (as before) ======================== //

    public static class UpdatePasswordRequest {
        private String newPassword;

        // Getters and Setters
        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
