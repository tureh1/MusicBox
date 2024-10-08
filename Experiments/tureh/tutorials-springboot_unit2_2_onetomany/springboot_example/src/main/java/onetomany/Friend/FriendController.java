package onetomany.Friend;

import onetomany.Users.User;
import onetomany.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    // Get all friends for a specific user
    @GetMapping("/user/{userId}")
    public List<Friend> getFriendsByUser(@PathVariable int userId) {
        return friendRepository.findByUserIdAndIsAccepted(userId, true); // Only show accepted friends
    }

    // Send friend request
    @PostMapping("/request")
    public String sendFriendRequest(@RequestParam int userId, @RequestParam int friendId) {
        User user = userRepository.findById(userId);
        User friend = userRepository.findById(friendId);

        if (user == null || friend == null) {
            return failure; // User or friend does not exist
        }

        Friend friendRequest = new Friend(user, friend);
        friendRepository.save(friendRequest);

        return success;
    }

    // Accept friend request
    @PutMapping("/accept/{friendId}")
    public String acceptFriendRequest(@PathVariable int friendId) {
        Friend friendRequest = friendRepository.findById(friendId);

        if (friendRequest == null || friendRequest.getIsAccepted()) {
            return failure; // No friend request found or already accepted
        }

        friendRequest.setIsAccepted(true);
        friendRepository.save(friendRequest);

        return success; // Friend request accepted
    }

    // Decline friend request
    @DeleteMapping("/decline/{friendId}")
    public String declineFriendRequest(@PathVariable int friendId) {
        Friend friendRequest = friendRepository.findById(friendId);

        if (friendRequest == null) {
            return failure; // No friend request found
        }

        friendRepository.delete(friendRequest);
        return success; // Friend request declined
    }
}
