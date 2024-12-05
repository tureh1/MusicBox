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

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button loginButton;
    private Button deleteButton;
    private TextView forgotPassword;
    private TextView signUpLink;
    private boolean isFirstClick = true; // Flag to check if it's the first click

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        deleteButton = findViewById(R.id.DeleteButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        signUpLink = findViewById(R.id.signup);

        // Set up listeners
        setupListeners();
    }

    private void setupListeners() {
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

        // Login button click listener
        loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                sendLogInRequest(email.getText().toString().trim(), password.getText().toString().trim());
            }
        });

        // Delete button click listener
        deleteButton.setOnClickListener(v -> {
            if (validateInputs()) {
                sendDeleteRequest(email.getText().toString().trim());
            }
        });
    }

    // Validate login input
    private boolean validateInputs() {
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwordInput.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Send login request
    private void sendLogInRequest(String email, String password) {
        String url = "http://10.90.72.167:8080/login"; // Update URL as needed

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("emailId", email);
            requestData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                response -> {
                    try {
                        String message = response.getString("message");
                        Log.d("LoginActivity", "Response message: " + message);

                        if (message.equalsIgnoreCase("login successful")) {
                            int userId = response.getInt("userId");

                            storeUserDataInSharedPreferences(email, userId);

                            Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainPage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("LoginActivity", "JSON Parsing error", e);
                        Toast.makeText(LoginActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LoginActivity", "Login request error", error);
                    Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void sendDeleteRequest(String email) {
        String url = "http://10.90.72.167:8080/users/" + email;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        if ("success".equalsIgnoreCase(message)) {
                            Toast.makeText(this, "User deleted successfully.", Toast.LENGTH_SHORT).show();
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

    // Store user data in SharedPreferences
    private void storeUserDataInSharedPreferences(String email, int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("emailId", email);
        editor.putInt("userId", userId);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }
}