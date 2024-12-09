package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddSongActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton backButton;
    private AddPlaylistAdapter adapter;
    private List<Song> songList;
    private String userEmail;
    private int userId;
    private long playlistId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_song);

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("emailId", null);
        userId = sharedPreferences.getInt("userId", -1);

        // Retrieve playlistId from the Intent
        long playlistId = getIntent().getLongExtra("playlistId", -1);

        if (playlistId == -1) {
            Log.e("AddSongActivity", "Invalid playlistId received.");
            Toast.makeText(this, "Missing playlist ID.", Toast.LENGTH_SHORT).show();
            finish(); // Optionally close the activity if playlistId is mandatory
        } else {
            Log.d("AddSongActivity", "Received playlistId: " + playlistId);
            // Use the playlistId for your operations, such as adding songs to this playlist
        }

        // Initialize views
        recyclerView = findViewById(R.id.songsRecyclerView);
        backButton = findViewById(R.id.backButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize song list and adapter
        songList = new ArrayList<>();
        adapter = new AddPlaylistAdapter(songList, this);
        recyclerView.setAdapter(adapter);


        // Set up back button click listener
        backButton.setOnClickListener(v -> finish());
        fetchAvailableSongs();

    }


    private void fetchAvailableSongs() {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/songs";
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("AddSongActivity", "Response: " + response.toString());  // Log the entire response
                    try {
                        songList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject songJson = response.getJSONObject(i);
                            int id = (int) songJson.optLong("id", -1);
                            String title = songJson.optString("title", "Unknown Title");
                            String artist = songJson.optString("artist", "Unknown Artist");
                            String coverUrl = songJson.optString("cover", "");

                            songList.add(new Song(id, title, artist, coverUrl));
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter about data changes
                    } catch (JSONException e) {
                        Log.e("AddSongActivity", "Error fetching available songs", e);
                        Toast.makeText(this, "Error fetching available songs", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("AddSongActivity", "Error fetching songs: " + error.toString());
                    Toast.makeText(this, "Error fetching available songs", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }



    private void addSongToPlaylist(Song song) {
        if (userId == -1 || playlistId == -1) {
            Toast.makeText(this, "User ID or Playlist ID is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://coms-3090-048.class.las.iastate.edu:8080/users/" + userId +
                "/playlists/" + playlistId + "/songs";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("songId", song.getId());
        } catch (JSONException e) {
            Log.e("AddSongActivity", "Error creating PUT request body: " + e.getMessage());
            Toast.makeText(this, "Error adding song to playlist.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            Toast.makeText(this, "Song added to playlist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to add song.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("AddSongActivity", "Error parsing PUT response: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("AddSongActivity", "Error adding song to playlist: " + error.toString());
                    Toast.makeText(this, "Network error adding song.", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}
