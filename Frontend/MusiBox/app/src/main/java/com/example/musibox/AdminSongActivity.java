package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminSongActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText searchBar;
    private AdminSongAdapter songAdapter;
    private List<Song> songList = new ArrayList<>();
    private List<Song> filteredSongList = new ArrayList<>();
    private ImageButton user, song,settings;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.search_bar);
        song = findViewById(R.id.music);
        user = findViewById(R.id.user);
        settings = findViewById(R.id.settings);

        user.setOnClickListener(v -> {
            Intent intent = new Intent(AdminSongActivity.this, AdminActivity.class);
            startActivity(intent);
        });
        song.setOnClickListener(v -> {
            Intent intent = new Intent(AdminSongActivity.this, AdminSongActivity.class);
            startActivity(intent);
        });
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(AdminSongActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = sharedPreferences.getString("emailId", null); // Default to null if not found
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if not found
        if (email != null && userId != -1) {
            Log.d("AdminSongActivity", "Logged-in email: " + email);
        } else {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songAdapter = new AdminSongAdapter(filteredSongList, new AdminSongAdapter.OnSongClickListener() {
            @Override
            public void onSongDelete(Song song) {
                // Call the method to delete the song from the backend
                deleteSongFromBackend(song.getId(), () -> {
                    // Remove the song from local lists
                    songList.remove(song);
                    filteredSongList.remove(song);
                    songAdapter.notifyDataSetChanged();  // Notify adapter to update RecyclerView
                });
            }
        });



        recyclerView.setAdapter(songAdapter);
        fetchSongData();

        setupSearchBar();
    }

    private void fetchSongData() {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/songs";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        songList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject songObject = response.getJSONObject(i);

                            // Fetch all necessary fields
                            String coverUrl = songObject.optString("cover", "");
                            String title = songObject.optString("title", "Unknown Song");
                            String artist = songObject.optString("artist", "Unknown Artist");
                            int id = songObject.optInt("id", -1);

                            // Create a Song object and add it to the list
                            Song song = new Song(id, title, artist, 0, coverUrl);  // Using 0 for average rating
                            songList.add(song);
                        }
                        filteredSongList.addAll(songList);
                        songAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing song data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching song data: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void deleteSongFromBackend(int songId, Runnable onSuccess) {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/songs/" + songId;

        JsonObjectRequest deleteRequest = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                response -> {
                    // Success - Song deleted
                    Toast.makeText(this, "Song deleted successfully!", Toast.LENGTH_SHORT).show();
                    onSuccess.run();  // Update UI after successful deletion
                },
                error -> {
                    // Error - Failed to delete song
                    Toast.makeText(this, "Failed to delete song: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(deleteRequest);
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().toLowerCase().trim();
                filterSongs(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void filterSongs(String query) {
        filteredSongList.clear();
        if (query.isEmpty()) {
            filteredSongList.addAll(songList);
        } else {
            for (Song song : songList) {
                if (song.getTitle().toLowerCase().contains(query) ||
                        song.getArtist().toLowerCase().contains(query)) {
                    filteredSongList.add(song);
                }
            }
        }
        songAdapter.notifyDataSetChanged();
    }
}