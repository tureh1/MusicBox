package com.example.musibox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class MainPage extends AppCompatActivity implements  WebSocketListener, RatingAdapter.OnRatingChangeListener {

    private ImageButton homeButton, addUserButton, messageButton, userButton;
    private RecyclerView recyclerView;
    private RatingAdapter ratingAdapter;
    private List<Song> songList = new ArrayList<>();
    private Map<String, Song> songMap = new HashMap<>();
    private WebSocketManager webSocketManager;
    private WebSocketClient webSocketClient;
    private WebSocket mWebSocket;
    private String userEmail;
    private EditText search_barAlbum;
    private int songId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = sharedPreferences.getString("emailId", null); // Default to null if not found
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if not found
        if (email != null && userId != -1 ) {
            // Use the email and userId to populate fields or make requests
            Log.d("ChatActivity", "Logged-in email: " + email);
        } else {
            // Handle missing data (e.g., redirect to login)
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Optionally finish this activity
        }
        userEmail = email;
        // Start the WebSocket connection immediately after the page loads


        // Fetch the song data
        fetchSongData();

        // Initialize WebSocketManager (if used elsewhere)
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.setWebSocketListener(this);

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
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // When text changes, perform search
                String query = charSequence.toString();

                // Check if the query is empty
                if (query.length() >= 0) {
                    searchForSong(query);  // Fetch and display all songs if the query is empty
                    fetchSongData();
                } else {
                    fetchSongData();  // Search for songs based on query
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Ensure we fetch all songs if the query is empty after backspacing
                if (editable.toString().isEmpty()) {
                    fetchSongData();
                }
            }
        });
    }

    private void initializeWebSocket() {
        URI uri = URI.create("ws://coms-3090-048.class.las.iastate.edu:8080/rate/" + userEmail);

        if (webSocketClient == null || !webSocketClient.isOpen()) {
            // Initialize the WebSocketClient once if it's not already connected
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("WebSocket", "WebSocket connected successfully.");
                }

                @Override
                public void onMessage(String message) {
                    Log.d("WebSocket", "Received message: " + message);
                    runOnUiThread(() -> updateAverageRatingUI(message));
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("WebSocket", "Connection closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    Log.e("WebSocket", "WebSocket error: ", ex);
                }
            };
            webSocketClient.connect();
        }
    }
    private void searchForSong(String query) {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/songs/search?query=" + query;

        // Using JsonArrayRequest to perform the search query
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Clear the previous search results
                            songList.clear();

                            // Iterate through the response and add the songs to the list
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject songObject = response.getJSONObject(i);
                                int songId = songObject.getInt("id");
                                String title = songObject.optString("title", "Unknown Song");
                                String artist = songObject.optString("artist", "Unknown Artist");
                                double avgRating = songObject.optDouble("averageRating", 0.0);

                                // Create a Song object and add it to the list
                                Song song = new Song(songId, title, artist, avgRating);
                                songList.add(song);
                            }

                            // Notify the adapter to update the view with the search results
                            ratingAdapter.notifyDataSetChanged();

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



    private void updateAverageRatingUI(String message) {

        try {
            JSONObject jsonMessage = new JSONObject(message);
            int songId = jsonMessage.getInt("songId");
            double newAverageRating = jsonMessage.getDouble("averageRating");

            // Find the song from the list
            for (Song song : songList) {
                if (song.getId() == songId) {
                    song.setAverageRating(newAverageRating);  // Update the average rating
                    break;  // Stop searching once we find the song
                }
            }

            // Notify the adapter to refresh the UI
            ratingAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("WebSocket", "Error parsing WebSocket message", e);
        }
    }

    // Fetch songs from the server and update the list
    private void fetchSongData() {
        // Start WebSocket connection if it's not already active
        if (userEmail != null) {
            URI uri = URI.create("ws://coms-3090-048.class.las.iastate.edu:8080/rate/" + userEmail);

            // Create a WebSocketClient to listen for song data updates
            WebSocketClient webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("WebSocket", "WebSocket connected successfully for song data.");
                }

                @Override
                public void onMessage(String message) {
                    Log.d("WebSocket", "Received song data update: " + message);
                    runOnUiThread(() -> {
                        try {
                            // Parse the received song data
                            JSONObject songData = new JSONObject(message);
                            int songId = songData.getInt("id");
                            String title = songData.optString("title", null);
                            String artist = songData.optString("artist", null);
                            // Parse the average rating. If it's not available, it should return a valid number, not -1.0
                            double avgRating = songData.optDouble("rating", -1);  // Using -1 as a fallback for invalid data

                            // Find the corresponding song from the map
                            Song song = songMap.get(String.valueOf(songId));

                            // If the song is new, add it to the list
                            if (song == null) {
                                song = new Song(songId, title, artist, avgRating);
                                songList.add(song);
                            } else {
                                // If it's an existing song, update its details
                                song.setTitle(title);
                                song.setArtist(artist);

                            ;
                            }

                            // Notify the adapter to refresh the RecyclerView
                            ratingAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("WebSocket", "Error parsing song data update: " + message);
                            Toast.makeText(MainPage.this, "Error parsing song data update", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("WebSocket", "Connection closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    Log.e("WebSocket", "WebSocket error: ", ex);
                }
            };

            webSocketClient.connect();
        }
    }




    // WebSocketListener methods
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "WebSocket opened: " + handshakedata.getHttpStatusMessage());
        runOnUiThread(() -> Toast.makeText(MainPage.this, "WebSocket Connection Opened", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onWebSocketMessage(String message) {
        Log.d("WebSocket", "Received message: " + message);
        runOnUiThread(() -> updateAverageRatingUI(message));
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "WebSocket closed: " + reason);
        runOnUiThread(() -> Toast.makeText(MainPage.this, "WebSocket Connection Closed", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "WebSocket error: ", ex);
    }

    // RatingAdapter.OnRatingChangeListener method
    @Override
    public void onRatingChanged(int songId, int rating) {
        if (userEmail != null) {
            URI uri = URI.create("ws://coms-3090-048.class.las.iastate.edu:8080/rate/" + userEmail);

            // Create a WebSocketClient for sending the rating
            WebSocketClient webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("WebSocket", "WebSocket connected for rating update.");

                    // Create a JSON object for the rating update
                    JSONObject jsonMessage = new JSONObject();
                    try {
                        jsonMessage.put("songId", songId);  // Use the passed songId here
                        jsonMessage.put("rating", rating);
                        send(jsonMessage.toString());
                        Log.d("WebSocket", "Sent rating update: " + jsonMessage.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("WebSocket", "Failed to create JSON for rating update.");
                    }
                }

                @Override
                public void onMessage(String message) {
                    Log.d("WebSocket", "Received rating update: " + message);
                    runOnUiThread(() -> {
                        try {
                            JSONObject response = new JSONObject(message);
                            String status = response.optString("status", "unknown");
                            String responseMessage = response.optString("message", "");

                            if ("success".equals(status)) {
                                Toast.makeText(MainPage.this, responseMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainPage.this, "Error: " + responseMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("WebSocket", "Error parsing received message: " + message);
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

            // Connect the WebSocket client
            webSocketClient.connect();
        }
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();  // Close WebSocket connection when the activity is destroyed
        }

}
}