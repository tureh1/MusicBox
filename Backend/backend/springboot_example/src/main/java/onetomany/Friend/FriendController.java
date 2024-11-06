package onetomany.Friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import onetomany.Users.User;
import onetomany.Users.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class FriendController {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @PutMapping(path = "/users/{userId}/friends/email/{friendEmail}")
    public ResponseEntity<String> updateFriendByEmail(@PathVariable int userId,
                                                      @PathVariable String friendEmail,
                                                      @RequestBody Friend updatedFriend) {
        // Check if the user exists
        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(userId));
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found\"}");
        }

        // Find the friend entry for the specified user and email
        Friend existingFriend = friendRepository.findByUserIdAndFriendEmail(userId, friendEmail);

        if (existingFriend != null) {
            // Update the friend name
            existingFriend.setFriendName(updatedFriend.getFriendName());

            // Save updated friend
            friendRepository.save(existingFriend);
            return ResponseEntity.ok(success);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Friend not found\"}"); // Friend not found
    }


    @PostMapping("/users/{userId}/addFriend")
    public ResponseEntity<String> addFriend(@PathVariable int userId, @RequestBody Map<String, String> friendRequest) {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(userId));
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found\"}");
        }

        User user = userOpt.get();
        String friendEmail = friendRequest.get("friendEmail");

        // Prevent self-addition
        if (user.getEmailId().equals(friendEmail)) {
            return ResponseEntity.badRequest().body("{\"error\": \"Cannot add yourself as a friend\"}");
        }

        // Validate friend email
        if (friendEmail == null || friendEmail.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Friend email is required\"}");
        }

        // Check if the friend entry already exists
        Friend friendEntry = friendRepository.findByUserIdAndFriendEmail(userId, friendEmail);

        // Allow re-adding if the friend exists but is not accepted
        if (friendEntry != null && !friendEntry.isAccepted()) {
            friendEntry.setAccepted(true);
            friendRepository.save(friendEntry);
            return ResponseEntity.ok("{\"message\": \"Friend re-added successfully\"}");
        } else if (friendEntry != null && friendEntry.isAccepted()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Friend already exists\"}");
        }

        // If no entry exists, create a new friend entry
        friendEntry = new Friend(user, friendEmail, false);
        friendRepository.save(friendEntry);

        // Check for mutual friend entry
        User friendUser = userRepository.findByEmailId(friendEmail);
        if (friendUser != null) {
            Friend mutualEntry = friendRepository.findByUserIdAndFriendEmail(friendUser.getId(), user.getEmailId());

            if (mutualEntry != null && !mutualEntry.isAccepted()) {
                friendEntry.setAccepted(true);
                mutualEntry.setAccepted(true);
                friendRepository.saveAll(List.of(friendEntry, mutualEntry));
                return ResponseEntity.ok("{\"message\": \"Friend added and accepted\"}");
            }
        }

        return ResponseEntity.ok("{\"message\": \"Friend request pending mutual acceptance\"}");
    }



    @GetMapping(path = "/users/{userId}/friends")
    public List<Friend> getFriendsByUser(@PathVariable int userId) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId));
        if (user.isPresent()) {
            // Retrieve only accepted friends
            return friendRepository.findByUserIdAndIsAccepted(userId, true);
        }
        return Collections.emptyList();
    }

    @GetMapping("/test/friends")
    public List<Friend> testFriends() {
        User user = new User();
        user.setId(1); // Set the user ID you want to test
        return friendRepository.findByUser(user);
    }

    @DeleteMapping(path = "/users/{userId}/friends/{friendEmail}")
    public ResponseEntity<String> deleteFriend(@PathVariable int userId, @PathVariable String friendEmail) {
        // Check if the user exists
        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(userId));
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found\"}");
        }

        // Find the friend entry for the specified user and email
        Friend friend = friendRepository.findByUserIdAndFriendEmail(userId, friendEmail);
        if (friend != null) {
            // Update isAccepted to false instead of deleting
            friend.setAccepted(false);
            friendRepository.save(friend);
            return ResponseEntity.ok(success);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failure); // Friend not found
    }


}

