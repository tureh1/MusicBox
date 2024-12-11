package onetomany.Users;

import java.util.List;
import java.util.Optional;

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

/**
 *
 * @author Tu Reh
 * User Feature for Login FInished
 * Implemented a role for Admin within Users
 * Login Method Checks if the role Admin exist within User
 */

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

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
            summary = "User login",
            description = "A user logs in with email and password"
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
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
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

        if (!existingUser.getIfActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Account is inactive\"}");
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


    @PostMapping("/users/{emailId}/status")
    @Operation(
            summary = "Toggle user's active status",
            description = "Toggle a user's active status between 1 (active) and 0 (inactive)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully toggled active status",
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
    public ResponseEntity<String> toggleUserStatus(@PathVariable String emailId) {
        User user = userRepository.findByEmailId(emailId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User not found\"}");
        }

        // Toggle the active status, ensuring that it's either true or false
        Boolean currentStatus = user.getIfActive();
        if (currentStatus == null) {
            currentStatus = false; // Default to false if it's null
        }
        user.setIfActive(!currentStatus);  // Switch the status (if it was true, set it to false and vice versa)
        userRepository.save(user);

        // Determine the response message based on the new status
        String responseMessage = user.getIfActive()
                ? "{\"message\":\"User successfully unbanned\"}"
                : "{\"message\":\"User successfully banned\"}";

        return ResponseEntity.ok(responseMessage);
    }


    @PutMapping(path = "/users/{id}/emailId")
    public ResponseEntity<String> updateUserEmail(@PathVariable int id, @RequestBody User userRequest) {
        // Validate input emailId
        if (userRequest == null || userRequest.getEmailId() == null || userRequest.getEmailId().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"Invalid input\"}");
        }

        // Check if the user exists
        Optional<User> existingUserOpt = Optional.ofNullable(userRepository.findById(id));
        if (!existingUserOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User not found\"}");
        }

        // Check if the new email already exists
        User existingEmailUser = userRepository.findByEmailId(userRequest.getEmailId());
        if (existingEmailUser != null && existingEmailUser.getId() != id) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\":\"Email already exists\"}");
        }

        // Update the email
        User userToUpdate = existingUserOpt.get();
        userToUpdate.setEmailId(userRequest.getEmailId());
        userRepository.save(userToUpdate);

        return ResponseEntity.ok("{\"message\":\"Email updated successfully\"}");
    }
}
