package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView usernames;
    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;
    private List<Song> songList;
    private String userId;
    private long playlistId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        // Retrieve userId from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = sharedPreferences.getString("emailId", null); // Default to null if not found
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if not found
        if (email != null && userId != -1 ) {
            // Use the email and userId to populate fields or make requests
            Log.d("ChatActivity", "Logged-in email: " + email);
        } else {
            // Handle missing data (e.g., redirect to login)
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();

            finish(); // Optionally finish this activity
        }

        // Retrieve GroupPlaylist object passed from previous activity
        GroupPlaylist groupPlaylist = getIntent().getParcelableExtra("groupPlaylist");

        if (groupPlaylist != null) {
            playlistId = groupPlaylist.getId();  // Initialize playlistId from GroupPlaylist object
            Log.d("PlaylistActivity", "Playlist ID: " + playlistId);
        } else {
            playlistId = getIntent().getIntExtra("playlistId", -1); // Fall back to intent if GroupPlaylist is not available
            Log.d("PlaylistActivity", "Playlist ID from Intent: " + playlistId);
        }


        // Initialize views
        backButton = findViewById(R.id.back);
        usernames = findViewById(R.id.usernames);
        recyclerView = findViewById(R.id.recyclerView);

        // Set RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songList = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(songList, this);
        recyclerView.setAdapter(playlistAdapter);

        String groupName = getIntent().getStringExtra("groupName");
        if (groupName != null)
            usernames.setText(groupName);

        // Fetch group playlist from backend
        fetchSongs();

        // Setup back button click listener
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchSongs() {
        // URL to fetch songs from
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/songs";
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", userId);
        playlistId = getIntent().getIntExtra("playlistId", -1);
        Log.d("PlaylistActivity", "Fetching songs from URL: " + url);

        // Create a JsonArrayRequest to fetch songs from the backend
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    // Log the response to check the structure
                    Log.d("PlaylistActivity", "Response: " + response.toString());

                    try {
                        // Clear the song list before adding new songs
                        songList.clear();

                        // Iterate through the array of songs
                        for (int i = 0; i < response.length(); i++) {

                            JSONObject songJson = response.getJSONObject(i);

                            // Get song details, ensuring null checks
                            String title = songJson.optString("title", "Unknown Title");  // Default if null
                            String artist = songJson.optString("artist", "Unknown Artist");  // Default if null

                            // Create a new Song object and add it to the list
                            Song song = new Song( title, artist);

                            songList.add(song);
                        }

                        // Notify the adapter that the data has been updated
                        playlistAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("PlaylistActivity", "Error parsing JSON response: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(PlaylistActivity.this, "Error parsing song data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log the error message
                    Log.e("PlaylistActivity", "Error fetching songs: " + error.getMessage());

                    // Check if the error has a network response with additional details
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        Log.e("PlaylistActivity", "Status code: " + statusCode);

                        // Get the response body for more information
                        String responseBody = new String(error.networkResponse.data);
                        Log.e("PlaylistActivity", "Response body: " + responseBody);
                    } else {
                        // If no network response, log the general error message
                        Log.e("PlaylistActivity", "Error fetching songs: " + error.getMessage());
                    }

                    // Show a Toast to notify the user about the error
                    Toast.makeText(PlaylistActivity.this, "Error fetching songs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the Volley request queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }


}



