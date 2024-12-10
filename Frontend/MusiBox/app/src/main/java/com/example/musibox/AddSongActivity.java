package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddSongActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private ImageButton backButton;
    private AddPlaylistAdapter adapter;
    private List<Song> songList;
    private List<Song> playlist;
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
        playlistId = getIntent().getLongExtra("playlistId", -1);

        if (playlistId == -1) {
            Log.e("AddSongActivity", "Invalid playlistId received.");
            Toast.makeText(this, "Missing playlist ID.", Toast.LENGTH_SHORT).show();
            finish(); // Optionally close the activity if playlistId is mandatory
        } else {
            Log.d("AddSongActivity", "Received playlistId: " + playlistId);
        }

        // Initialize views
        recyclerView = findViewById(R.id.songsRecyclerView);
        backButton = findViewById(R.id.backButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize song list and adapter
        songList = new ArrayList<>();
        playlist = new ArrayList<>();
        adapter = new AddPlaylistAdapter(songList, this, this::onAddSongClicked); // Pass the method to handle the click
        recyclerView.setAdapter(adapter);

        // Set up back button click listener
        backButton.setOnClickListener(v -> {
            // Set result to indicate that the playlist was updated
            Intent resultIntent = new Intent();
            resultIntent.putExtra("playlistUpdated", true); // You can send any information you want
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Fetch available songs to display in the RecyclerView
        fetchAvailableSongs();
    }

    private void fetchAvailableSongs() {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/songs";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("AddSongActivity", "Response: " + response.toString());
                    try {
                        songList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject songJson = response.getJSONObject(i);
                            int id = songJson.optInt("id", -1);
                            String title = songJson.optString("title", "Unknown Title");
                            String artist = songJson.optString("artist", "Unknown Artist");
                            String coverUrl = songJson.optString("cover", "");

                            songList.add(new Song(id, title, artist, coverUrl));
                        }
                        adapter.notifyDataSetChanged();
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

    // Method to handle adding song to playlist
    private void onAddSongClicked(Song song) {
        if (userId == -1 || playlistId == -1) {
            Toast.makeText(this, "User ID or Playlist ID is invalid.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the song is already in the playlist
        boolean isAlreadyInPlaylist = false;
        for (Song playlistSong : playlist) {
            if (playlistSong.getId() == song.getId()) {
                isAlreadyInPlaylist = true;
                break;
            }
        }

        if (isAlreadyInPlaylist) {
            Toast.makeText(this, "Song is already in the playlist.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://coms-3090-048.class.las.iastate.edu:8080/users/" + userId +
                "/playlists/" + playlistId + "/songs/" + song.getId();

        // Prepare the POST request
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("AddSongActivity", "Song added successfully.");
                    Toast.makeText(this, song.getTitle() + " has been added to the playlist.", Toast.LENGTH_SHORT).show();
                    fetchPlaylistSongs(userId, playlistId); // Refresh the playlist
                },
                error -> {
                    Log.e("AddSongActivity", "Failed to add song: " + error.toString());
                    Toast.makeText(this, "Failed to add song to playlist", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }



    private void fetchPlaylistSongs(int userId, long playlistId) {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/users/" + userId + "/playlists/" + playlistId + "/songs";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        playlist.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject songJson = response.getJSONObject(i);
                            int id = songJson.optInt("id", -1);
                            String title = songJson.optString("title", "Unknown Title");
                            String artist = songJson.optString("artist", "Unknown Artist");
                            String coverUrl = songJson.optString("cover", "");

                            playlist.add(new Song(id, title, artist, coverUrl));
                        }
                        adapter.notifyDataSetChanged();  // Notify adapter to update UI
                    } catch (JSONException e) {
                        Log.e("AddSongActivity", "Error parsing playlist response", e);
                        Toast.makeText(this, "Error fetching playlist songs", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("AddSongActivity", "Error fetching playlist songs: " + error.toString());
                    Toast.makeText(this, "Error fetching playlist songs", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }
}
