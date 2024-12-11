package onetomany.Profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import onetomany.Users.User;
import onetomany.Users.UserRepository;
import onetomany.Friend.Friend;
import onetomany.Friend.FriendRepository;

import java.util.*;

@RestController
//@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    private final FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int defaultBackgroundColor = 0xFF000000;

    public ProfileController(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    // Get the number of friends for a user
    @GetMapping("/profiles/users/{userId}/friendCount")
    @Operation(
            summary = "Get the number of friends for a user",
            description = "Retrieves the count of accepted friends for a specific user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved friend count",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public int getFriendsByUser(@PathVariable int userId) {
        List<Friend> friends = friendRepository.findByUserIdAndIsAccepted(userId, true); // Only show accepted friends
        return friends.size(); // Return the size of the list
    }

    // Get the bio of a user
    @GetMapping("/users/{userId}/profile/bio")
    @Operation(
            summary = "Get a user's bio",
            description = "Fetches the bio of a specific user based on their profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved bio",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or profile not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public String getUserBio(@PathVariable int userId) {
        Optional<Profile> profile = profileRepository.findByUserId(userId);
        return profile.map(Profile::getBio).orElse("No bio found"); // Return the bio or a default message if not found
    }

    // Create a post method for a user bio
    @PostMapping("/users/{userId}/profile/bio")
    @Operation(
            summary = "Create a user's bio",
            description = "Adds a new bio for the user, creating their profile if it doesn't exist."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bio created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public Profile createUserBio(@PathVariable int userId, @RequestBody String bio) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId));
        if (user.isPresent()) {
            Profile profile = new Profile(user.get(), bio);
            profile.setBackgroundColor(defaultBackgroundColor);  // Set default background color
            return profileRepository.save(profile); // Save the profile with bio
        }
        return null; // Or throw an exception if user not found
    }

    // Create a put method for user bio
    @PutMapping("/users/{userId}/profile/bio")
    @Operation(
            summary = "Update a user's bio",
            description = "Updates the existing bio for the user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bio updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public Profile updateUserBio(@PathVariable int userId, @RequestBody String newBio) {
        Optional<Profile> profile = profileRepository.findByUserId(userId);
        if (profile.isPresent()) {
            Profile updatedProfile = profile.get();
            updatedProfile.setBio(newBio); // Update the bio
            return profileRepository.save(updatedProfile); // Save the updated profile
        }
        return null; // Or throw an exception if profile not found
    }

    // Create a delete method for a user bio
    @DeleteMapping("/users/{userId}/profile/bio")
    @Operation(
            summary = "Delete a user's bio",
            description = "Removes the bio for the user, deleting their profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bio deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public String deleteUserBio(@PathVariable int userId) {
        Optional<Profile> profile = profileRepository.findByUserId(userId);
        if (profile.isPresent()) {
            profileRepository.delete(profile.get()); // Delete the profile (or just the bio if needed)
            return "Bio deleted successfully";
        }
        return "Profile not found"; // Or throw an exception if profile not found
    }

    // Get the user's background color
    @GetMapping("/users/{userId}/profile/color")
    public ResponseEntity<Map<String, String>> getUserColor(@PathVariable int userId) {
        Optional<Profile> profileOptional = profileRepository.findByUserId(userId);

        if (profileOptional.isPresent()) {
            Profile profile = profileOptional.get();
            int color = profile.getBackgroundColor();

            String colorHex = String.format("%08X", color);
            Map<String, String> response = new HashMap<>();
            response.put("backgroundColor", colorHex);
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("backgroundColor", "#FF000000"); // Default color if not found
            return ResponseEntity.ok(response);
        }
    }

    // Update the user's background color
    @PutMapping("/users/{userId}/profile/color")
    @Transactional
    public ResponseEntity<String> updateUserColor(@PathVariable int userId, @RequestBody ColorUpdateRequest request) {
        int color = (int) Long.parseLong(request.getBackgroundColor(), 16); // Convert hex color string to integer

        Optional<Profile> profileOptional = profileRepository.findByUserId(userId);
        if (profileOptional.isPresent()) {
            Profile profile = profileOptional.get();
            profile.setBackgroundColor(color); // Update the color field
            profileRepository.save(profile);  // Save the updated profile
            return ResponseEntity.ok("Color updated successfully!");
        } else {
            // Create new profile for the user if it doesn't exist
            User user = userRepository.findById(userId);
            if (user != null) {
                Profile newProfile = new Profile(user, ""); // Create a profile with a default bio (or provide one)
                newProfile.setBackgroundColor(color); // Set the background color
                profileRepository.save(newProfile); // Save the new profile
                return ResponseEntity.ok("Profile created and color updated successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        }
    }



}