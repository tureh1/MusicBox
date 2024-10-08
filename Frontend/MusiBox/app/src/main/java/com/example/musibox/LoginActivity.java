package com.example.musibox;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button loginButton;
    TextView forgotPassword;
    TextView signUpLink;
    private boolean isFirstClick = true; // Flag to check if it's the first click



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        signUpLink = findViewById(R.id.signup);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ForgotPasswordActivity or show a Toast for now
                Toast.makeText(LoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
            }
        });


        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to SignUpActivity or show a Toast for now


                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        // Common focus change listener for both username and password fields
        View.OnFocusChangeListener clearTextListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus && isFirstClick) {
                    email.setText("");  // Clear the default text from username
                    password.setText("");  // Clear the default text from password
                    isFirstClick = false;  // Ensure this only happens once for both fields
                }
            }
        };
        // Attach the common listener to both fields
        email.setOnFocusChangeListener(clearTextListener);
        password.setOnFocusChangeListener(clearTextListener);

        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handleLogIn()) {
                    // Send email and password to the backend
                    sendLogInRequest(email.getText().toString().trim(), password.getText().toString().trim());
                }
            }
        });

    }

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


    private void sendLogInRequest(String email, String password) {
        String url = "http://10.90.72.167:8080/login"; // Your backend URL

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

                        // Check the message to determine if the login was successful
                        if (message.equals("login successful")) {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();


                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
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

    private void fetchUserData(String userId) {
        String url = "http://10.90.72.167:8080/user/" + userId;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Handle the response
                    try {
                        //fetched user data
                        String email = response.getString("emailId");


                        // Store the fetched data for later use.
                        getSharedPreferences("user_data", MODE_PRIVATE)
                                .edit()
                                .putString("userId", userId)
                                .putString("email", email)
                                .apply();

                        // Navigate to the next activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle error
                    Toast.makeText(LoginActivity.this, "Failed to fetch user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the GET request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(getRequest);
    }
    
}
