package onetomany.Friend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
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
    @Operation(
            summary = "update friend details",
            description = "update the name of a friend"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated friend",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "friend not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
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
    @Operation(
            summary = "Add or accept a friend request",
            description = "Adds a new friend or accepts a mutual friend request"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added or accepted friend",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<String> addFriend(@PathVariable int userId, @RequestBody Map<String, String> friendRequest) {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(userId));
        if (userOpt.isEmpty()) {
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

        // Check if the friend exists
        User friendUser = userRepository.findByEmailId(friendEmail);
        if (friendUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Friend user not found\"}");
        }

        // Check for an existing entry (user -> friend)
        Friend friendEntry = friendRepository.findByUserIdAndFriendEmail(userId, friendEmail);

        // If the friendEntry exists and is already accepted, do nothing
        if (friendEntry != null && friendEntry.isAccepted()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Friend already exists\"}");
        }

        // Check if a pending mutual request exists (friend -> user)
        Friend mutualEntry = friendRepository.findByUserIdAndFriendEmail(friendUser.getId(), user.getEmailId());
        if (mutualEntry != null) {
            if (!mutualEntry.isAccepted()) {
                // If the reverse request is pending, mark both as accepted
                mutualEntry.setAccepted(true);
                if (friendEntry == null) {
                    friendEntry = new Friend(user, friendEmail, true);
                } else {
                    friendEntry.setAccepted(true);
                }
                friendRepository.saveAll(List.of(friendEntry, mutualEntry));
                return ResponseEntity.ok("{\"message\": \"Friendship established mutually\"}");
            } else {
                // If the reverse request is already accepted, do nothing
                return ResponseEntity.badRequest().body("{\"error\": \"Friendship already established\"}");
            }
        }

        // If no mutual request exists, create or update the request as pending
        if (friendEntry == null) {
            friendEntry = new Friend(user, friendEmail, false);
            friendRepository.save(friendEntry);
            return ResponseEntity.ok("{\"message\": \"Friend request sent\"}");
        } else if (!friendEntry.isAccepted()) {
            return ResponseEntity.ok("{\"message\": \"Friend request is still pending\"}");
        }

        return ResponseEntity.badRequest().body("{\"error\": \"Unhandled state\"}");
    }



    @GetMapping(path = "/users/{userId}/friends")
    @Operation(
            summary = "Get a user's friends",
            description = "Fetches the friends of a user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved friends",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public List<Friend> getFriendsByUser(@PathVariable int userId) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId));
        if (user.isPresent()) {
            // Retrieve only accepted friends
            return friendRepository.findByUserIdAndIsAccepted(userId, true);
        }
        return Collections.emptyList();
    }

    @DeleteMapping(path = "/users/{userId}/friends/{friendEmail}")
    @Operation(
            summary = "Delete a user's friend",
            description = "Marks the friendship as pending on both sides, allowing re-addition"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted friend",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invalid id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<String> deleteFriend(@PathVariable int userId, @PathVariable String friendEmail) {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(userId));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found\"}");
        }

        // Retrieve the user and friend records
        Friend friend = friendRepository.findByUserIdAndFriendEmail(userId, friendEmail);
        if (friend != null) {
            User friendUser = userRepository.findByEmailId(friendEmail);

            // Handle mutual friendship
            if (friendUser != null) {
                Friend mutualFriend = friendRepository.findByUserIdAndFriendEmail(friendUser.getId(), userOpt.get().getEmailId());
                if (mutualFriend != null) {
                    // Mark mutual friendship as pending
                    mutualFriend.setAccepted(false);
                    friend.setAccepted(false);
                    friendRepository.saveAll(List.of(friend, mutualFriend));
                    return ResponseEntity.ok("{\"message\": \"Friendship marked as pending\"}");
                }
            }

            // If only one side exists, mark as pending
            friend.setAccepted(false);
            friendRepository.save(friend);
            return ResponseEntity.ok("{\"message\": \"Friendship marked as pending\"}");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Friend not found\"}");
    }
}
