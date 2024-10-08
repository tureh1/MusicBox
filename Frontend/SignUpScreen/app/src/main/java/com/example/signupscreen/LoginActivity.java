package com.example.signupscreen;

import android.annotation.SuppressLint;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button loginButton;
    TextView forgotPassword;
    TextView signUpLink;
    private boolean isFirstClick = true; // Flag to check if it's the first click
    private static final String BASE_URL = "http://coms-3090-048.class.las.iastate.edu:8080"; // Replace with your backend URL
    private RequestQueue requestQueue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        // Initialize views
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        signUpLink = findViewById(R.id.signUp);

        // Initialize the Volley request queue
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        // Set up Forgot Password click listener
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up Sign Up link click listener
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        // Attach focus change listener to clear default text
        View.OnFocusChangeListener clearTextListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus && isFirstClick) {
                    username.setText("");  // Clear the default text from username
                    password.setText("");  // Clear the default text from password
                    isFirstClick = false;  // Ensure this only happens once for both fields
                }
            }
        };
        username.setOnFocusChangeListener(clearTextListener);
        password.setOnFocusChangeListener(clearTextListener);

        // Set up Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = username.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if (validateFields(emailInput, passwordInput)) {
                    // Send login request to backend
                    sendLoginRequest(emailInput, passwordInput);
                }
            }
        });
    }

    // Validate input fields
    private boolean validateFields(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Send login request using Volley
    private void sendLoginRequest(String email, String password) {
        String url = BASE_URL + "/login";

        // Create a JSON object with email and password
        JSONObject loginData = new JSONObject();
        try {
            loginData.put("emailId", email);
            loginData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a JsonObjectRequest for the POST request
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, loginData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");  // Assuming the backend returns a "success" boolean

                            if (success) {
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                // Navigate to the home screen or another activity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(loginRequest);
    }
}