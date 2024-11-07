package com.example.musibox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPage extends AppCompatActivity implements WebSocketListener, RatingAdapter.OnRatingChangeListener {

    private ImageButton homeButton, addUserButton, messageButton, userButton;
    private RecyclerView recyclerView;
    private RatingAdapter ratingAdapter;
    private List<Song> songList = new ArrayList<>();
    private Map<String, Song> songMap = new HashMap<>();
    private WebSocketClient webSocketClient;
    private String userEmail;
    private EditText search_barAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = sharedPreferences.getString("emailId", null); // Default to null if not found
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if not found
        if (email != null && userId != -1) {
            Log.d("MainPage", "Logged-in email: " + email);
        } else {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        userEmail = email;

        // Initialize WebSocket when the activity starts
        initializeWebSocket();

        // Initialize buttons and RecyclerView
        homeButton = findViewById(R.id.navigation_home);
        addUserButton = findViewById(R.id.navigation_adduser);
        messageButton = findViewById(R.id.navigation_message);
        userButton = findViewById(R.id.navigation_user);
        search_barAlbum = findViewById(R.id.search_barAlbum);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        ratingAdapter = new RatingAdapter(this, songList, this); // Pass this as the listener for both song clicks and rating changes
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ratingAdapter);

        // Button listeners (navigation)
        homeButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, MainPage.class)));
        addUserButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, CreateGroupActivity.class)));
        messageButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, MessageActivity.class)));
        userButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, UserProfileActivity.class)));

        // TextWatcher for search functionality
        search_barAlbum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString();
                if (query.length() >= 0) {
                    searchForSong(query);  // Fetch and display songs based on query
                    fetchSongData();       // Fetch data to refresh the list
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    fetchSongData(); // Fetch all songs if query is empty
                }
            }
        });

        // Fetch the song data initially
        fetchSongData();
    }

    // WebSocket initialization and lifecycle management
    private void initializeWebSocket() {
        if (userEmail != null && webSocketClient == null) {
            URI uri = URI.create("ws://coms-3090-048.class.las.iastate.edu:8080/rate/" + userEmail);

            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("WebSocket", "WebSocket connected.");
                }

                @Override
                public void onMessage(String message) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonMessage = new JSONObject(message);
                            updateAverageRatingUI(jsonMessage);
                        } catch (JSONException e) {
                            Log.e("WebSocket", "Error parsing WebSocket message", e);
                        }
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("WebSocket", "WebSocket closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    Log.e("WebSocket", "WebSocket error: ", ex);
                }
            };

            webSocketClient.connect();
        }
    }

    // Search for songs by query
    private void searchForSong(String query) {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/songs/search?query=" + query;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        songList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject songObject = response.getJSONObject(i);
                            int songId = songObject.getInt("id");
                            String title = songObject.optString("title", "Unknown Song");
                            String artist = songObject.optString("artist", "Unknown Artist");
                            double avgRating = songObject.optDouble("averageRating", 0.0);

                            Song song = new Song(songId, title, artist, avgRating);
                            songList.add(song);
                        }
                        ratingAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(MainPage.this, "Error parsing song data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(MainPage.this, "Error fetching song data: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    // Update average rating in the UI when received from WebSocket
    private void updateAverageRatingUI(JSONObject jsonMessage) {
        try {
            int songId = jsonMessage.getInt("songId");
            double newAverageRating = jsonMessage.getDouble("averageRating");

            for (Song song : songList) {
                if (song.getId() == songId) {
                    song.setAverageRating(newAverageRating);
                    break;
                }
            }

            ratingAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("WebSocket", "Error updating rating UI", e);
        }
    }

    // Fetch song data (this could trigger WebSocket activity or server calls)
    private void fetchSongData() {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/songs";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        songList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject songObject = response.getJSONObject(i);
                            int songId = songObject.getInt("id");
                            String title = songObject.optString("title", "Unknown Song");
                            String artist = songObject.optString("artist", "Unknown Artist");
                            double avgRating = songObject.optDouble("averageRating", 0.0);

                            Song song = new Song(songId, title, artist, avgRating);
                            songList.add(song);
                        }
                        ratingAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(MainPage.this, "Error parsing song data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(MainPage.this, "Error fetching song data: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    // Handle rating changes and send them through WebSocket
    @Override
    public void onRatingChanged(int songId, int rating) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            try {
                JSONObject ratingUpdate = new JSONObject();
                ratingUpdate.put("songId", songId);
                ratingUpdate.put("rating", rating);
                webSocketClient.send(ratingUpdate.toString());
            } catch (JSONException e) {
                Log.e("WebSocket", "Failed to send rating update", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close(); // Close WebSocket when the activity is destroyed
        }
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        // Called when the WebSocket connection is opened
        Log.d("WebSocket", "Connection opened: " + handshakedata);
    }

    @Override
    public void onWebSocketMessage(String message) {
        // Called when a message is received from the server
        runOnUiThread(() -> {
            try {
                // Assuming the message is a JSON string that includes song rating info
                JSONObject jsonMessage = new JSONObject(message);
                updateAverageRatingUI(jsonMessage);  // Custom method to update the UI
            } catch (JSONException e) {
                Log.e("WebSocket", "Error parsing WebSocket message", e);
            }
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        // Called when the WebSocket connection is closed
        Log.d("WebSocket", "Connection closed: Code = " + code + ", Reason = " + reason);
        Toast.makeText(MainPage.this, "WebSocket closed: " + reason, Toast.LENGTH_SHORT).show();

        // Optional: Reconnect logic if you want to try reconnecting
        // initializeWebSocket(); // Uncomment if you want to automatically reconnect
    }

    @Override
    public void onWebSocketError(Exception ex) {
        // Called when there's an error with the WebSocket connection
        Log.e("WebSocket", "Error: ", ex);
        Toast.makeText(MainPage.this, "WebSocket Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
