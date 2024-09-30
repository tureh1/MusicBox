package onetomany.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Keeping only necessary fields for signup
    private String emailId;
    private String password;
    private Boolean ifActive = true; // or false, based on your logic

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

}
