package com.example.musibox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private String userId; // Added field for userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView usernameProfile = findViewById(R.id.username_profile);
        bioProfile = findViewById(R.id.bio_profile);
        saveBio = findViewById(R.id.save_bio_text);
        deleteBio = findViewById(R.id.delete_bio_text);
        friendsCount = findViewById(R.id.friends_count);

        friendsCount.setOnClickListener(v -> {
                    // Action to perform when the message button is clicked
                    Intent intent1 = new Intent(UserProfileActivity.this, CreateGroupActivity.class);
                    startActivity(intent1);
                });
        Intent intent = getIntent();
        String email = intent.getStringExtra("emailId");
        userId = intent.getStringExtra("userId");
        
        // Initialize the Volley request queue
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        if (email != null) {
            usernameProfile.setText(email);
        } else {
            usernameProfile.setText("No email provided");
        }

        getBio();


        saveBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bioText = bioProfile.getText().toString().trim(); // Get bio text
                saveBio(bioText);
                if (!bioText.isEmpty()) {
                    // Call saveBio to send the POST request with bio text
                    saveBio(bioText);
                }
            }
        });

        deleteBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBio();
            }
        });


    }

    private void getBio(){
        String url = "http://10.90.72.167:8080/users/" + userId + "/bio";
        //String url = "http://10.90.72.167:8080/users/6/bio";

        // Create a GET request with Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Parse the bio from the JSON response and set it to bioProfile
                    try {
                        String bioText = response.getString("bio"); // Assuming the response has a "bio" field
                        bioProfile.setText(bioText); // Set the bio to the EditText
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UserProfileActivity.this, "Error parsing bio", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle error response
                    String errorMessage = "Failed to fetch bio: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage += "\nResponse Code: " + error.networkResponse.statusCode
                                + "\nResponse Data: " + new String(error.networkResponse.data);
                    }
                    Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });

        // Add the request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void saveBio(String bioText) {
        String url = "http://10.90.72.167:8080/users/" + userId + "/bio";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("bio", bioText);  // Add bio text to JSON object
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    // Handle the successful response
                    Toast.makeText(UserProfileActivity.this, "Bio updated successfully!", Toast.LENGTH_SHORT).show();
                    bioProfile.setText(bioText);
                },
                error -> {
                    // Handle error response
                    String errorMessage = "Failed to update bio: " + error.getMessage();
                    Log.e("UserProfileActivity", errorMessage);  // Log the error for debugging
                    Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");  // Set the Content-Type header
                return headers;
            }
        };

          // Add the request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        // Add the request to the Volley queue

    }
    private void deleteBio() {

        String url = "http://10.90.72.167:8080/users/" + userId + "/bio";

        // Create a DELETE request with Volley
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    // Handle successful response
                    bioProfile.setText(""); // Clear the EditText after deletion
                    Toast.makeText(UserProfileActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error response
                    String errorMessage = "Failed to delete bio: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    if (error.networkResponse != null) {
                        errorMessage += "\nResponse Code: " + error.networkResponse.statusCode
                                + "\nResponse Data: " + new String(error.networkResponse.data);
                    }
                    Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });

        // Add the request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


}
