package onetomany.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";
    private String signupSuccess = "{\"message\":\"signup successfully\"}";
    private String signupFailure = "{\"message\":\"email already registered\"}";
    private String passwordChangeSuccess = "{\"message\":\"password reset successfully\"}";
    private String passwordChangeFailure = "{\"message\":\"password reset unsuccessfully\"}";
    private String loginSuccessTemplate = "{\"message\":\"login successful\", \"userId\": %d}";
    private String loginFailure = "{\"message\":\"invalid email or password\"}";

    @GetMapping(path = "/users")
    List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping(path = "/signup")
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
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(id));
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }


    @PostMapping(path = "/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        if (user == null || user.getEmailId() == null || user.getPassword() == null ||
                user.getEmailId().trim().isEmpty() || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"Invalid input\"}");
        }

        User existingUser = userRepository.findByEmailId(user.getEmailId());
        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            // Email or password mismatch
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Invalid email or password\"}");
        }

        // Login successful, return userId and success message
        String successMessage = String.format("{\"message\":\"Login successful\", \"userId\": %d}", existingUser.getId());
        return ResponseEntity.ok(successMessage);
    }


    @DeleteMapping(path = "/users/{emailId}")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String emailId) {
        User user = userRepository.findByEmailId(emailId);
        if (user != null) {
            userRepository.delete(user);
            return ResponseEntity.ok(success);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failure);
    }


    @PutMapping(path = "/newpass/{emailId}")
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
}

