package com.example.musibox;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * LoginActivity handles user authentication, including login and account deletion,
 * and navigation to other activities like Sign Up and Forgot Password.
 * This class utilizes the Volley library for network requests and SharedPreferences
 * for storing user session data.
 */
public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button loginButton;
    Button deleteButton;
    TextView forgotPassword;
    TextView signUpLink;
    private boolean isFirstClick = true; // Flag to check if it's the first click

    /**
     * Called when the activity is first created.
     * Sets up UI components, initializes click listeners, and configures network requests.
     *
     * @param savedInstanceState If the activity is being reinitialized after previously being shut down, this Bundle contains the saved data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        deleteButton = findViewById(R.id.DeleteButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        signUpLink = findViewById(R.id.signup);

        // Forgot Password click listener
        forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(intent);
        });

        // Sign Up click listener
        signUpLink.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUp.class);
            startActivity(intent);
        });

        // Focus change listener to clear text on first click
        View.OnFocusChangeListener clearTextListener = (view, hasFocus) -> {
            if (hasFocus && isFirstClick) {
                email.setText("");
                password.setText("");
                isFirstClick = false;
            }
        };
        email.setOnFocusChangeListener(clearTextListener);
        password.setOnFocusChangeListener(clearTextListener);

        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        // Login button click listener
        loginButton.setOnClickListener(v -> {
            if (handleLogIn()) {
                sendLogInRequest(email.getText().toString().trim(), password.getText().toString().trim());
            }
        });

        // Delete button click listener
        deleteButton.setOnClickListener(v -> {
            if (handleLogIn()) {
                DeleteRequest(email.getText().toString().trim());
            }
        });
    }

    /**
     * Validates the user input for login credentials.
     *
     * @return true if the input is valid, false otherwise.
     */
    private boolean handleLogIn() {
        String usernameInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwordInput.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Sends a login request to the backend server.
     *
     * @param email    The user's email address.
     * @param password The user's password.
     */
    private void sendLogInRequest(String email, String password) {
        String url = "http://10.90.72.167:8080/login"; // Backend URL

        // Create a JSON object with the email and password
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("emailId", email);
            requestData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the JsonObjectRequest for the POST request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                response -> {
                    // Handle the success response here
                    try {
                        String message = response.getString("message");
                        Log.d("LoginActivity", "Response message: " + message);

                        // Check the message to determine if the login was successful
                        if (message.equalsIgnoreCase("login successful")) {
                            int userId = response.getInt("userId"); // Retrieve user ID
                            Log.d("LoginActivity", "User ID: " + userId);

                            // Store user data in SharedPreferences
                            storeUserDataInSharedPreferences(email, userId);

                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                            // Start MainPage activity
                            Intent intent = new Intent(LoginActivity.this, MainPage.class);
                            Log.d("LoginActivity", "Starting MainPage");
                            startActivity(intent);
                            finish(); // Remove login activity from the back stack
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle the error here
                    Toast.makeText(LoginActivity.this, "Log in Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Stores user session data in SharedPreferences.
     *
     * @param email  The user's email address.
     * @param userId The user's unique ID.
     */
    private void storeUserDataInSharedPreferences(String email, int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("emailId", email); // Store email
        editor.putInt("userId", userId);   // Store user ID
        editor.putBoolean("isLoggedIn", true);  // Mark user as logged in
        editor.apply();  // Apply changes
    }

    /**
     * Sends a delete request to the backend server to delete a user account.
     *
     * @param email The email of the user to be deleted.
     */
    private void DeleteRequest(String email) {
        String url = "http://10.90.72.167:8080/users/" + email; // Email passed in the URL

        // Create the JsonObjectRequest for the DELETE request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    // Handle the success response here
                    try {
                        String message = response.getString("message");
                        Log.d("LoginActivity", "Delete Response message: " + message);

                        // Check the message to determine if the delete was successful
                        if (message.equals("success")) {
                            Toast.makeText(LoginActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle the error here
                    Toast.makeText(LoginActivity.this, "Deletion failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Logs out the user by clearing their session data from SharedPreferences.
     */
    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // This will remove all entries
        editor.apply();  // Apply changes

        // Optionally, redirect to login screen after logout
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();  // Close current activity
    }
}