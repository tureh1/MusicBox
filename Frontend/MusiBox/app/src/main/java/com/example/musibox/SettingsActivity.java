package com.example.musibox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {
    private EditText editPassword, confirmPassword;
    private MaterialButton updatePasswordButton, logoutButton, deleteButton;
    private String userEmail;
    private ImageButton back;
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String KEY_BUTTON_COLOR = "button_color";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Retrieve the logged-in user's email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("emailId", null); // Ensure the email was saved in SharedPreferences

        if (userEmail == null) {
            Toast.makeText(this, "Error: No logged-in user found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user is logged in
        }

        // Initialize views
        deleteButton = findViewById(R.id.delete);
        editPassword = findViewById(R.id.edit_password);
        confirmPassword = findViewById(R.id.confirm_password);
        updatePasswordButton = findViewById(R.id.update_password_button);
        logoutButton = findViewById(R.id.logout);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
        // Set up the password update functionality
        updatePasswordButton.setOnClickListener(v -> {
            if (validatePasswordInputs()) {
                // Send the password update request
                sendPasswordUpdateRequest(userEmail, editPassword.getText().toString().trim());
            }
        });
        logoutButton.setOnClickListener(v -> {
            logout();
        });
        deleteButton.setOnClickListener(v -> {
            sendDeleteRequest(userEmail);
        });
    }


    // Validate the password inputs
    private boolean validatePasswordInputs() {
        String passwordInput = editPassword.getText().toString().trim();
        String confirmInput = confirmPassword.getText().toString().trim();

        if (passwordInput.isEmpty() || confirmInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwordInput.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!passwordInput.equals(confirmInput)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Send the password update request
    private void sendPasswordUpdateRequest(String email, String newPassword) {
        String url = "http://10.90.72.167:8080/newpass/" + email; // Update endpoint as per your backend

        // Create a JSON object with the new password
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("newPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the JsonObjectRequest for the PUT request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, requestData,
                response -> {
                    // Handle the success response
                    try {
                        String message = response.getString("message");
                        Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Optionally log out the user after password change
                        logout();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SettingsActivity.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle the error response
                    Toast.makeText(SettingsActivity.this, "Failed to update password: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the request to the Volley queue
        com.example.musibox.VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    private void sendDeleteRequest(String email) {
        String url = "http://10.90.72.167:8080/users/" + email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        if ("success".equalsIgnoreCase(message)) {
                            Toast.makeText(this, "User deleted successfully.", Toast.LENGTH_SHORT).show();
                            logout();
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("LoginActivity", "JSON Parsing error", e);
                        Toast.makeText(this, "Unexpected response from server.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LoginActivity", "Delete request error", error);
                    Toast.makeText(this, "Delete failed. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    // Logout method (optional)
    private void logout() {
        // Clear the saved user data
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences.edit();
        editor.clear(); // Remove all saved user data
        editor.apply();
        editor1.clear();
        editor1.apply();

        Toast.makeText(this, "Password updated. Please log in again.", Toast.LENGTH_SHORT).show();

        // Redirect to LoginActivity
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish(); // Close the SettingsActivity
    }
}