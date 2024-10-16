package onetomany.Friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import onetomany.Users.User;
import onetomany.Users.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class FriendController {

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    UserRepository userRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @PutMapping(path = "/friends/{friendId}")
    public String updateFriend(@PathVariable int friendId, @RequestBody Friend updatedFriend) {
        Optional<Friend> friendOptional = friendRepository.findById(friendId);
        if (friendOptional.isPresent()) {
            Friend existingFriend = friendOptional.get();

            // Update fields
            existingFriend.setFriendName(updatedFriend.getFriendName());

            // Save updated friend
            friendRepository.save(existingFriend);

            return success;
        }
        return failure; // Friend not found
    }

    @PostMapping(path = "/users/{userId}/addFriend")
    public String addFriend(@PathVariable int userId, @RequestBody Friend friend) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId));
        if (user.isPresent()) {
            friend.setUser(user.get());
            friendRepository.save(friend);
            return success;
        }
        return failure;
    }

    @GetMapping(path = "/users/{userId}/friends")
    public List<Friend> getFriendsByUser(@PathVariable int userId) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId));
        if (user.isPresent()) {
            List<Friend> friends = friendRepository.findByUser(user.get());
            System.out.println("Number of friends fetched: " + friends.size()); // Debug line
            return friends;
        }
        return Collections.emptyList(); // Return empty list if user not found
    }

    @GetMapping("/test/friends")
    public List<Friend> testFriends() {
        User user = new User();
        user.setId(1); // Set the user ID you want to test
        return friendRepository.findByUser(user);
    }


    @DeleteMapping(path = "/friends/{friendId}")
    public String deleteFriend(@PathVariable int friendId) {
        try {
            friendRepository.deleteById(friendId);
            return success;
        } catch (Exception e) {
            return failure;
        }
    }
}
