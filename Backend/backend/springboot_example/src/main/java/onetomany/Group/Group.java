package onetomany.Group;

import jakarta.persistence.*;
import onetomany.Users.User;
import onetomany.Users.UserRepository;
//import javax.persistence.*;
import java.util.List;

@Entity
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "group_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    public Group() {}

    // Getters and Setters

    // Getter for id
    public Long getId() {
        return id;
    }

    // Setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for users
    public List<User> getUsers() {
        return users;
    }

    // Setter for users
    public void setUsers(List<User> users) {
        this.users = users;
    }

}
