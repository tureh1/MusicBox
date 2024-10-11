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
                if (!bioText.isEmpty()) {
                    // Call saveBio to send the POST request with bio text
                    saveBio(bioText);
                } else {
                    Toast.makeText(UserProfileActivity.this, "Bio cannot be empty", Toast.LENGTH_SHORT).show();
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

    private void saveBio(String bioText){
        String url = "http://10.90.72.167:8080/users/" + userId + "/bio";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("bio", bioText); // Add bio text
        } catch (JSONException e) {
            e.printStackTrace();
            return;  // Return early if JSON creation fails
        }
        // Create a POST request with Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    // Handle successful response
                    Toast.makeText(UserProfileActivity.this, "Bio saved successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error response
                    String errorMessage = "Failed to save bio: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage += "\nResponse Code: " + error.networkResponse.statusCode
                                + "\nResponse Data: " + new String(error.networkResponse.data);
                    }
                    Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });

        // Add the request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void deleteBio(){
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
                    String errorMessage = "Failed to delete bio: " + error.getMessage();
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
