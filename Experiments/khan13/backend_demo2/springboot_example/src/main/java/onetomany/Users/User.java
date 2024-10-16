package onetomany.Users;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import onetomany.Profile.Profile;

import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Keeping only necessary fields for signup
    private String emailId;
    private String password;
    private Boolean ifActive = true; // or false, based on your logic

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Profile profile;
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

    public Profile getProfile() { // Change to single Profile
        return profile;
    }

    public void setProfile(Profile profile) { // Change to single Profile
        this.profile = profile;
    }

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
