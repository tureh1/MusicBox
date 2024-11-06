package onetomany.Group;

import onetomany.Users.User;
import onetomany.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @PostMapping("/create")
    public Group createGroup(@RequestParam String name) {
        // Fetch all users from the UserRepository
        List<User> allUsers = userRepository.findAll();

        // Create a new Group with all users
        Group group = new Group();

        // Save and return the Group
        return groupRepository.save(group);
    }
}
