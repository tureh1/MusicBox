package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private EditText bioProfile;
    private TextView friendsCount;
    private Button saveBio, deleteBio;
    private String userId;
    private ImageButton house, addUserButton, messageButton, userButton, settingsButton, colorPickerButton;
    private ImageView profilePicture;
    private int currentBackgroundColor = Color.BLACK; // Initial background color
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String KEY_BUTTON_COLOR = "button_color";



    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
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
        colorPickerButton = findViewById(R.id.color_picker_button);
        profilePicture = findViewById(R.id.avatar_profile);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentBackgroundColor = preferences.getInt(KEY_BUTTON_COLOR, Color.BLACK); // Default to BLACK

        // Apply the saved color to the button
        colorPickerButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(currentBackgroundColor));

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = sharedPreferences.getString("emailId", null); // Default to null if not found
        userId = String.valueOf(sharedPreferences.getInt("userId", -1)); // Default to -1 if not found

        if (email != null && !userId.equals("-1")) {
            Log.d("UserProfile", "Logged-in email: " + email);
        } else {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (email != null) {
            usernameProfile.setText(email);
        } else {
            usernameProfile.setText("No email provided");
        }


        getUserBackgroundColor(Integer.parseInt(userId));
        getBio(Integer.parseInt(userId));
        getFriendsCount(Integer.parseInt(userId));
        setupNavigationButtons();

        saveBio.setOnClickListener(view -> {
            String bioText = bioProfile.getText().toString().trim();
            if (!bioText.isEmpty()) {
                saveBio(bioText, Integer.parseInt(userId));
            }
        });

        profilePicture.setOnClickListener(v -> openGallery());


        deleteBio.setOnClickListener(view -> deleteBio(Integer.parseInt(userId)));

        colorPickerButton.setOnClickListener(view -> openColorPickerDialog());

    }
    // Open the gallery to pick an image using the new photo picker
    private void openGallery() {
        // Launch the photo picker for images only
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback when user selects an image or cancels
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);

                    // Use Glide to load the image into the ImageView with a circular transformation
                    Glide.with(this)
                            .load(uri)
                            .circleCrop()  // This will crop the image into a circle
                            .into(profilePicture);

                    // Optionally, you can set a scale type, though Glide handles image sizing well
                    profilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);  // Or FIT_CENTER based on your preference
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    private void setupNavigationButtons() {
        house.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, MainPage.class)));
        addUserButton.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, CreateGroupActivity.class)));
        messageButton.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, MessageActivity.class)));
        userButton.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, UserProfileActivity.class)));
        settingsButton.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, SettingsActivity.class)));
    }



    private void deleteBio(int userId) {

        String url = "http://10.90.72.167:8080/users/" + userId + "/profile/bio";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    bioProfile.setText("");
                    Toast.makeText(UserProfileActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    String errorMessage = "Failed to delete bio: " + error.getMessage();
                });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void saveBio(String bioText, int userId) {
        String url = "http://10.90.72.167:8080/users/" + userId + "/profile/bio";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("bio", bioText);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    Toast.makeText(UserProfileActivity.this, "Bio updated successfully!", Toast.LENGTH_SHORT).show();
                    bioProfile.setText(bioText);
                },
                error -> {
                    String errorMessage = "Failed to update bio: " + error.getMessage();
                    Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    private void getBio(int userId) {
        String url = "http://10.90.72.167:8080/users/" + userId + "/profile/bio";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (!response.has("bio")) {

                            return;
                        }
                        String bioText = response.getString("bio");
                        bioProfile.setText(bioText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UserProfileActivity.this, "Error parsing bio", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Failed to fetch bio: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage += "\nResponse Code: " + error.networkResponse.statusCode;
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    private void getFriendsCount(int userId) {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/profiles/users/" + userId + "/friendCount";

        Log.d("UserProfileActivity", "GET Friends Count URL: " + url); // Log the GET request URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("UserProfileActivity", "Friends Count Response: " + response);

                    try {
                        // Since the backend returns just an integer (the count), parse it directly
                        int count = Integer.parseInt(response); // Convert the response to an integer
                        friendsCount.setText("Friends: " + count); // Set the friends count in the TextView
                    } catch (NumberFormatException e) {
                        Log.e("UserProfileActivity", "Error parsing friends count: " + e.getMessage());
                        friendsCount.setText("Error fetching friends count");
                    }
                },
                error -> {
                    String errorMessage = "Failed to fetch friends count: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage += "\nResponse Code: " + error.networkResponse.statusCode
                                + "\nResponse Data: " + new String(error.networkResponse.data);
                    }
                    Log.e("UserProfileActivity", errorMessage); // Log the error
                    Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void getUserBackgroundColor(int userId) {
        String url = "http://10.90.72.167:8080/users/" + userId + "/profile/color";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Check if the backgroundColor field exists and is not null
                        if (response.has("backgroundColor") && !response.isNull("backgroundColor")) {
                            String colorHex = response.getString("backgroundColor");

                            // Ensure the color string starts with a "#" before parsing
                            if (!colorHex.startsWith("#")) {
                                colorHex = "#" + colorHex;
                            }

                            // Attempt to parse the color
                            int colorInt = Color.parseColor(colorHex);
                            changeBackgroundColor(colorInt);
                        } else {
                            Log.e("UserProfile", "backgroundColor field is missing or null in the response.");
                            Toast.makeText(getApplicationContext(),
                                    "No color found. Using default.", Toast.LENGTH_SHORT).show();

                            // Use a default color if not found
                            changeBackgroundColor(Color.BLACK); // Default background color
                        }
                    } catch (JSONException | IllegalArgumentException e) {
                        // Handle JSON parsing errors or invalid color format errors
                        Log.e("UserProfile", "Error parsing background color: " + e.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Failed to retrieve or parse color. Using default.", Toast.LENGTH_SHORT).show();

                        // Use a default color in case of errors
                        changeBackgroundColor(Color.BLACK);
                    }
                },
                error -> {
                    // Handle network errors
                    Log.e("UserProfile", "Network error while fetching color: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Failed to retrieve color. Please try again later.", Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the Volley request queue
        VolleySingleton.getInstance(this).addToRequestQueue(getRequest);
    }


    private void updateUserColor(int selectedColor,int userId) {
        // Include alpha channel in the color hex
        String colorHex = String.format("%08X", selectedColor);
        String url = "http://10.90.72.167:8080/users/" + userId + "/profile/color";

        JSONObject colorData = new JSONObject();
        try {
            colorData.put("backgroundColor", colorHex);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to prepare data for color update", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, colorData,
                response -> {
                    // Log the full response for debugging
                    Log.d("UserProfile", "Response: " + response.toString());
                    Toast.makeText(getApplicationContext(), "Color updated successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("UserProfile", "Updated color: " + colorHex);
                },
                error -> {
                    String errorMessage = "Failed to update color: " + error.getMessage();

                    if (error.networkResponse != null) {
                        String responseBody = new String(error.networkResponse.data);
                        errorMessage += "\nResponse Code: " + error.networkResponse.statusCode +
                                "\nResponse Body: " + responseBody;
                        Log.e("UserProfile", errorMessage);
                    }

                   
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                // Add authorization token if required
                return headers;
            }
        };

        // Add the request to the queue
        Volley.newRequestQueue(this).add(putRequest);
    }


    private void openColorPickerDialog() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(currentBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("OK", (dialog, selectedColor, allColors) -> {
                    // Only update when user confirms the selection
                    if (selectedColor != currentBackgroundColor) {
                        changeBackgroundColor(selectedColor);
                        updateUserColor(selectedColor, Integer.parseInt(userId)); // Now update color after selection
                    }
                })
                .setNegativeButton("Cancel", null)
                .build()
                .show();
    }



    private void changeBackgroundColor(int color) {
        currentBackgroundColor = color;
        colorPickerButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
    }

}