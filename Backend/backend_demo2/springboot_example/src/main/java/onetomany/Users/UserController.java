package onetomany.Users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping(path = "/users/{emailId}")
    User getUserById(@PathVariable String emailId) {
        return userRepository.findByEmailId(emailId); // Updated to use emailId as String
    }

    @PostMapping(path = "/users")
    String createUser(@RequestBody User user) {
        if (user == null || user.getEmailId() == null || user.getPassword() == null)
            return failure;
        userRepository.save(user);
        return success;
    }

    @DeleteMapping(path = "/users/{id}")
    String deleteUser(@PathVariable int id){
        userRepository.deleteById(id);
        return success;
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