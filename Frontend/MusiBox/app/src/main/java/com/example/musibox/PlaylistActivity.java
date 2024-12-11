package com.example.musibox;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private ImageButton backButton, addButton;
    private TextView usernames;
    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;
    private List<Song> songList;
    private String userEmail;
    int userId;
    long playlistId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Retrieve userId and playlistId from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("emailId", null); // Default to null if not found
        userId = sharedPreferences.getInt("userId", -1); // Default to -1 if not found
        Intent intent = getIntent();

        if (userId == -1) {
            Log.e("PlaylistActivity", "Invalid userId");
            Toast.makeText(this, "Missing user ID", Toast.LENGTH_SHORT).show();
        } else {
            // Fetch playlists to determine playlistId
            fetchPlaylists(userId);
        }

        // Initialize views
        backButton = findViewById(R.id.back);
        addButton = findViewById(R.id.addButton);
        usernames = findViewById(R.id.usernames);
        recyclerView = findViewById(R.id.recyclerView);

        // Set RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songList = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(songList, this);
        recyclerView.setAdapter(playlistAdapter);

        String groupName = getIntent().getStringExtra("groupName");
        if (groupName != null) {
            usernames.setText(groupName);
        }

        // Setup back button click listener
        backButton.setOnClickListener(v -> finish());

        addButton.setOnClickListener(v -> {
            if (playlistId != -1) { // Ensure playlistId is valid
                Intent intent1 = new Intent(PlaylistActivity.this, AddSongActivity.class);
                intent1.putExtra("playlistId", playlistId); // Pass playlistId to AddSongActivity
                startActivity(intent1);
            } else {
                Toast.makeText(this, "Playlist ID is not set.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Fetch playlists
    private void fetchPlaylists(int userId) {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/users/" + userId + "/playlists/myPlaylists";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject playlistJson = response.getJSONObject(i);
                            long id = playlistJson.optLong("id", -1);
                            String name = playlistJson.optString("name", "Unknown Playlist");

                            // Find the playlist based on the group name
                            if (name.equals(getIntent().getStringExtra("groupName"))) {
                                playlistId = id;
                                break;
                            }
                        }

                        if (playlistId != -1) {
                            fetchSongs(userId, playlistId);
                        } else {
                            Log.e("PlaylistActivity", "Playlist not found!");
                            Toast.makeText(this, "Playlist not found.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("PlaylistActivity", "Error parsing playlist response: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing playlist data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("PlaylistActivity", "Error fetching playlists: " + error.toString());
                    Toast.makeText(this, "Error fetching playlists.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    // Fetch songs for the playlist
    private void fetchSongs(int userId, long playlistId) {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/users/" + userId + "/playlists/" + playlistId + "/songs";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
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
                        playlistAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("PlaylistActivity", "Error parsing song response: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing song data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("PlaylistActivity", "Error fetching songs: " + error.toString());
                    Toast.makeText(this, "Error fetching songs.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }


    public void removeSong(int userId, long playlistId, int songId) {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/users/" + userId +
                "/playlists/" + playlistId + "/songs/" + songId;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("PlaylistActivity", "Song removed successfully.");
                    fetchSongs(userId, playlistId); // Refresh playlist after removal
                },
                error -> Toast.makeText(this, "Failed to remove song from playlist", Toast.LENGTH_SHORT).show());

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }



}
