package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    private EditText bioProfile;
    private TextView friendsCount;
    private Button saveBio, deleteBio;
    private int userId; // Updated to use int type
    private ImageButton house, addUserButton, messageButton, userButton, settingsButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = sharedPreferences.getString("emailId", null); // Default to null if not found
        userId = sharedPreferences.getInt("userId", -1); // Default to -1 if not found
        if (email != null && userId != -1) {
            Log.d("UserProfileActivity", "Logged-in email: " + email);
            Log.d("UserProfileActivity", "Logged-in userId: " + userId); // Log the userId
        } else {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        TextView usernameProfile = findViewById(R.id.username_profile);
        bioProfile = findViewById(R.id.bio_profile);
        saveBio = findViewById(R.id.save_bio_text);
        deleteBio = findViewById(R.id.delete_bio_text);
        friendsCount = findViewById(R.id.friends_count);
        house = findViewById(R.id.home);
        addUserButton = findViewById(R.id.adduser);
        messageButton = findViewById(R.id.message);
        userButton = findViewById(R.id.user);
        settingsButton = findViewById(R.id.settings);

        Intent intent = getIntent();

        setupNavigationButtons();

        // Initialize the Volley request queue
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        if (email != null) {
            usernameProfile.setText(email);
        } else {
            usernameProfile.setText("No email provided");
        }

        getBio();

        saveBio.setOnClickListener(view -> {
            String bioText = bioProfile.getText().toString().trim();
            if (!bioText.isEmpty()) {
                saveBio(bioText);
            }
        });

        deleteBio.setOnClickListener(view -> deleteBio());
    }

    private void setupNavigationButtons() {
        house.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, MainPage.class)));
        addUserButton.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, CreateGroupActivity.class)));
        messageButton.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, MessageActivity.class)));
        userButton.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, UserProfileActivity.class)));
        settingsButton.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, SettingsActivity.class)));
    }

    private void getBio() {
        String url = "http://10.90.72.167:8080/users/" + userId + "/bio";
        Log.d("UserProfileActivity", "GET Bio URL: " + url); // Log the GET request URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Log the raw response for debugging
                    Log.d("UserProfileActivity", "Raw Response: " + response);

                    // Check if the response is in JSON format (starts with "{" and ends with "}")
                    if (response.startsWith("{") && response.endsWith("}")) {
                        try {
                            // Try parsing the response as JSON
                            JSONObject jsonResponse = new JSONObject(response);
                            String bioText = jsonResponse.getString("bio");
                            bioProfile.setText(bioText); // Set the bio in the EditText
                        } catch (JSONException e) {
                            Log.e("UserProfileActivity", "Error parsing bio JSON: " + e.getMessage());
                            bioProfile.setText("Error parsing bio.");
                        }
                    } else {
                        // If the response is plain text, display it directly
                        bioProfile.setText(response);
                        Log.d("UserProfileActivity", "Raw Bio: " + response);
                        Toast.makeText(UserProfileActivity.this, "Bio is in an unexpected format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Failed to fetch bio: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage += "\nResponse Code: " + error.networkResponse.statusCode
                                + "\nResponse Data: " + new String(error.networkResponse.data);
                    }
                    Log.e("UserProfileActivity", errorMessage); // Log the error
                    Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void saveBio(String bioText) {
        String url = "http://10.90.72.167:8080/users/" + userId + "/bio";
        Log.d("UserProfileActivity", "POST Bio URL: " + url); // Log the POST request URL

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("bio", bioText);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Toast.makeText(UserProfileActivity.this, "Bio updated successfully!", Toast.LENGTH_SHORT).show();
                    bioProfile.setText(bioText);
                },
                error -> {
                    String errorMessage = "Failed to update bio: " + error.getMessage();
                    Log.e("UserProfileActivity", errorMessage); // Log the error
                    Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void deleteBio() {
        String url = "http://10.90.72.167:8080/users/" + userId + "/bio";
        Log.d("UserProfileActivity", "DELETE Bio URL: " + url); // Log the DELETE request URL

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    bioProfile.setText("");
                    Toast.makeText(UserProfileActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    String errorMessage = "Failed to delete bio: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    if (error.networkResponse != null) {
                        errorMessage += "\nResponse Code: " + error.networkResponse.statusCode
                                + "\nResponse Data: " + new String(error.networkResponse.data);
                    }
                    Log.e("UserProfileActivity", errorMessage); // Log the error
                    Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
