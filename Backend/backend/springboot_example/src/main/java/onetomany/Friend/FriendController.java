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

        // Prevent adding oneself
        if (user.getEmailId().equals(friendEmail)) {
            return ResponseEntity.badRequest().body("{\"error\": \"Cannot add yourself as a friend\"}");
        }

        // Ensure friendEmail is provided
        if (friendEmail == null || friendEmail.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Friend email is required\"}");
        }

        // Check if a friend request already exists (user -> friend)
        Friend friendEntry = friendRepository.findByUserIdAndFriendEmail(userId, friendEmail);
        if (friendEntry != null && friendEntry.isAccepted()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Friend already exists\"}");
        }

        // Check if there is a mutual friend request (friend -> user)
        User friendUser = userRepository.findByEmailId(friendEmail);
        if (friendUser != null) {
            Friend mutualEntry = friendRepository.findByUserIdAndFriendEmail(friendUser.getId(), user.getEmailId());

            // If mutualEntry exists and is pending, accept both requests
            if (mutualEntry != null && !mutualEntry.isAccepted()) {
                // Accept the mutual request and the current request
                if (friendEntry == null) {
                    // Create a new friend entry for the current user
                    friendEntry = new Friend(user, friendEmail, true);
                } else {
                    // If a request already exists but is pending, update it to accepted
                    friendEntry.setAccepted(true);
                }

                // Accept the mutual entry
                mutualEntry.setAccepted(true);
                friendRepository.saveAll(List.of(friendEntry, mutualEntry));

                return ResponseEntity.ok("{\"message\": \"Friend request accepted and mutual friendship established\"}");
            }
        }

        // Create a new pending friend request if no mutual entry exists
        if (friendEntry == null) {
            friendEntry = new Friend(user, friendEmail, false);
            friendRepository.save(friendEntry);
            return ResponseEntity.ok("{\"message\": \"Friend request pending mutual acceptance\"}");
        }

        return ResponseEntity.ok("{\"message\": \"Friend request already sent, waiting for mutual acceptance\"}");
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
        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(userId));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found\"}");
        }

        // Find the friend entry for the specified user and email
        Friend friend = friendRepository.findByUserIdAndFriendEmail(userId, friendEmail);
        if (friend != null) {
            // Remove the mutual friendship (on both sides) if it exists
            User friendUser = userRepository.findByEmailId(friendEmail);
            if (friendUser != null) {
                Friend mutualFriend = friendRepository.findByUserIdAndFriendEmail(friendUser.getId(), userOpt.get().getEmailId());
                if (mutualFriend != null) {
                    friendRepository.delete(mutualFriend); // Delete the mutual friend entry
                }
            }

            // Delete the friend entry for the user
            friendRepository.delete(friend);
            return ResponseEntity.ok(success);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failure);
    }


}
