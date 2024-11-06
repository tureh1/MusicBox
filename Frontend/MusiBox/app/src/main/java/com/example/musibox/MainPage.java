package com.example.musibox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.android.volley.toolbox.JsonArrayRequest;

public class MainPage extends AppCompatActivity implements RatingAdapter.OnSongClickListener {

    private ImageButton homeButton, addUserButton, messageButton, userButton;
    private RecyclerView recyclerView;
    private RatingAdapter ratingAdapter;
    private List<Song> songList = new ArrayList<>();
    private Map<String, Song> songMap = new HashMap<>();
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        fetchSongData();

        // Initialize buttons and RecyclerView
        homeButton = findViewById(R.id.navigation_home);
        addUserButton = findViewById(R.id.navigation_adduser);
        messageButton = findViewById(R.id.navigation_message);
        userButton = findViewById(R.id.navigation_user);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        ratingAdapter = new RatingAdapter(this, songList, this); // Pass this as the listener
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ratingAdapter);

        // Button listeners (navigation)
        homeButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, MainPage.class)));
        addUserButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, FriendsActivity.class)));
        messageButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, MessageActivity.class)));
        userButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, UserProfileActivity.class)));
    }

    private void fetchSongData() {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/songs";

        // Using JsonArrayRequest instead of JsonObjectRequest
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Log the response to check the structure
                            Log.d("Volley Response", response.toString());

                            // Iterate through the array and extract the required fields
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject songObject = response.getJSONObject(i);
                                int songId = songObject.getInt("id");
                                String title = songObject.optString("title", "Unknown Song");
                                String artist = songObject.optString("artist", "Unknown Artist");
                                float avgRating = (float) songObject.optDouble("averageRating", 0);

                                // Create a Song object and add it to the list
                                Song song = new Song(songId, title, artist, avgRating);
                                songList.add(song);
                                songMap.put(String.valueOf(songId), song);
                            }

                            // Update the adapter with the new song list
                            ratingAdapter.updateSongList(songList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainPage.this, "Error parsing song data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainPage.this, "Error fetching song data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    public void onSongClick(int songId) {
        Song clickedSong = songMap.get(String.valueOf(songId));
        if (clickedSong != null) {
            // Handle the song click, e.g., show a toast or navigate to a new screen
            Toast.makeText(this, "Song clicked: " + clickedSong.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }
}