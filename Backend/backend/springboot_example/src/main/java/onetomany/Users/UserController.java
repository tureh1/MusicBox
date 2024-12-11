package onetomany.Users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Tu Reh
 *
 */

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    // Default color is black (#000000 or 0x000000)
    private static final int DEFAULT_COLOR = 0x000000;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";
    private String signupSuccess = "{\"message\":\"signup successfully\"}";
    private String signupFailure = "{\"message\":\"email already registered\"}";
    private String passwordChangeSuccess = "{\"message\":\"password reset successfully\"}";
    private String passwordChangeFailure = "{\"message\":\"password reset unsuccessfully\"}";
    private String loginSuccessTemplate = "{\"message\":\"login successful\", \"userId\": %d}";
    private String loginFailure = "{\"message\":\"invalid email or password\"}";

    @GetMapping(path = "/users")
    @Operation(
            summary = "get all users",
            description = "fetches all users that have an account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added song",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping(path = "/signup")
    @Operation(
            summary = "a user signing up",
            description = "a user signs up for application with email and password"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully signed up",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public String signupUser(@RequestBody User user) {
        if (user == null || user.getEmailId() == null || user.getPassword() == null ||
                user.getEmailId().trim().isEmpty() || user.getPassword().trim().isEmpty()) {
            return signupFailure; // Invalid input
        }

        User existingUser = userRepository.findByEmailId(user.getEmailId());
        if (existingUser != null) {
            return signupFailure; // User with this email already exists
        }

        try {
            userRepository.save(user);
        } catch (Exception e) {
            return signupFailure; // Handle any exception that may occur while saving
        }

        return signupSuccess; // Signup successful
    }

    @GetMapping(path = "/users/{id}")
    @Operation(
            summary = "get user",
            description = "fetches user based on id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully fetched user",
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
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(id));
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }


    @PostMapping(path = "/login")
    @Operation(
            summary = "user login",
            description = "a user logs in with email and password"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully logged in",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        if (user == null || user.getEmailId() == null || user.getPassword() == null ||
                user.getEmailId().trim().isEmpty() || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"Invalid input\"}");
        }

        User existingUser = userRepository.findByEmailId(user.getEmailId());
        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Invalid email or password\"}");
        }

        String role = existingUser.getIsAdmin() ? "admin" : "user";
        String successMessage = String.format(
                "{\"message\":\"Login successful\", \"userId\": %d, \"role\": \"%s\"}",
                existingUser.getId(),
                role
        );
        return ResponseEntity.ok(successMessage);
    }



    @DeleteMapping(path = "/users/{emailId}")
    @Operation(
            summary = "delete user",
            description = "deletes user based on id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted user",
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
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String emailId) {
        User user = userRepository.findByEmailId(emailId);
        if (user != null) {
            userRepository.delete(user);
            return ResponseEntity.ok(success);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failure);
    }


    @PutMapping(path = "/newpass/{emailId}")
    @Operation(
            summary = "change a user's password",
            description = "an existing user can change their password"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully changed password",
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

    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getAllUsersForAdmin(@RequestHeader("role") String role) {
        if (!"admin".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<String> deleteUserByAdmin(@RequestHeader("role") String role, @PathVariable int id) {
        if (!"admin".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"Access denied\"}");
        }

        User user = userRepository.findById(id);
        if (user != null) {
            userRepository.delete(user);
            return ResponseEntity.ok("{\"message\":\"User deleted successfully\"}");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User not found\"}");
    }

    @PostMapping("/admin/create")
    public ResponseEntity<String> createAdmin(@RequestHeader("role") String role, @RequestBody User adminRequest) {
        if (!"admin".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"Access denied\"}");
        }

        if (adminRequest == null || adminRequest.getEmailId() == null || adminRequest.getPassword() == null ||
                adminRequest.getEmailId().trim().isEmpty() || adminRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"Invalid input\"}");
        }

        // Check if an account with the provided email already exists
        User existingUser = userRepository.findByEmailId(adminRequest.getEmailId());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\":\"Email already registered\"}");
        }

        // Create a new admin user
        User newAdmin = new User();
        newAdmin.setEmailId(adminRequest.getEmailId());
        newAdmin.setPassword(adminRequest.getPassword());
        newAdmin.setIsAdmin(true); // Set admin status to true

        // Save the admin to the database
        userRepository.save(newAdmin);

        return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\":\"Admin created successfully\"}");
    }



    public String updateUserPassword(@PathVariable String emailId, @RequestBody User.UpdatePasswordRequest updateRequest) {
        User existingUser = userRepository.findByEmailId(emailId);
        if (existingUser == null) {
            return passwordChangeFailure; // User not found
        }
        String newPassword = updateRequest.getNewPassword();
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return passwordChangeFailure; // Invalid password
        }

        existingUser.setPassword(newPassword);
        userRepository.save(existingUser);
        return passwordChangeSuccess; // Password updated successfully
    }
    /*
    @PutMapping(path = "/users/{id}/color")
    public ResponseEntity<String> updateUserColor(@PathVariable int id, @RequestBody String color) {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(id));

        return userOpt.map(user -> {
            // Update the user's color
            user.setColor(color);
            userRepository.save(user);
            return ResponseEntity.ok("{\"message\":\"Color updated successfully\"}");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User not found\"}"));
    }

    @GetMapping(path = "/users/{id}/color")
    public ResponseEntity<String> getUserColor(@PathVariable int id) {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(id));

        // Return the color if found, otherwise return the default color "0x000000"
        return userOpt.map(user -> {
            // Get the user's color or return the default "0x000000" if it's not set
            String color = user.getColor();
            String formattedColor = (color != null && color.startsWith("#")) ? "0x" + color.substring(1) : "0x000000";
            return ResponseEntity.ok(formattedColor);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("0x000000"));
    }
*/
    /*
    @PutMapping("/users/{userId}/color")
    public ResponseEntity<String> updateUserColor(@PathVariable int userId, @RequestBody String colorHex) {
        try {
            if (!colorHex.matches("^#[0-9A-Fa-f]{8}$")) {
                return ResponseEntity.badRequest().body("Invalid color format");
            }
            User user = userRepository.findById(userId);//.orElseThrow(() -> new RuntimeException("User not found"));
            user.setColor(colorHex);
            userRepository.save(user);
            return ResponseEntity.ok("Color updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating color");
        }
    }

    @GetMapping("/users/{userId}/color")
    public ResponseEntity<String> getUserColor(@PathVariable int userId) {
        try {
            User user = userRepository.findById(userId);//.orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user.getColor());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching color");
        }
    }
*/

    @PutMapping("/users/{userId}/color")
    @Transactional
    public ResponseEntity<String> updateUserColor(@PathVariable int userId, @RequestBody ColorUpdateRequest request) {
        // Convert the hex color string to an integer
        int color = (int) Long.parseLong(request.getBackgroundColor(), 16);

        // Find the user by ID using the custom repository method
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(userId));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setBackgroundColor(color);  // Update the color field
            userRepository.save(user);  // Save the updated user
            return ResponseEntity.ok("Color updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/users/{userId}/color")
    public ResponseEntity<Map<String, String>> getUserColor(@PathVariable int userId) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findById(userId));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            int color = user.getBackgroundColor();

            // Convert the integer color back to a hex string (AARRGGBB)
            String colorHex = String.format("%08X", color);

            Map<String, String> response = new HashMap<>();
            response.put("backgroundColor", colorHex);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}

