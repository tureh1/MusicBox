package onetomany.Profile;
import jakarta.persistence.*;
import onetomany.Users.User;

import java.util.Date;
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

   // @ManyToOne
    //@JoinColumn(name = "profile_id")
    //private User friend;

    //private Boolean isAccepted = false; // Whether the friendship has been accepted

    //@Temporal(TemporalType.TIMESTAMP)
    //private Date createdAt;

    // =============================== Constructors ================================== //
    public Profile() {
     //   this.createdAt = new Date(); // Initialize creation date
    }

    public Profile(User user, String bio) {
        this.user = user;
      //  this.friend = friend;
        this.bio = bio;
        //this.createdAt = new Date();
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
/*
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
 */
}
