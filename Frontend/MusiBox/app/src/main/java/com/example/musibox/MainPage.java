package com.example.musibox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class MainPage extends AppCompatActivity implements RatingAdapter.OnSongClickListener,WebSocketListener, RatingAdapter.OnRatingChangeListener{

    private ImageButton homeButton, addUserButton, messageButton, userButton;
    private RecyclerView recyclerView;
    private RatingAdapter ratingAdapter;
    private List<Song> songList = new ArrayList<>();
    private Map<String, Song> songMap = new HashMap<>();
    private WebSocketManager webSocketManager;
    private WebSocketClient webSocketClient;
    private WebSocket mWebSocket;
    private String userEmail;
    private int songId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null); // Default to null if not found
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if not found

        if (email != null && userId != -1) {
            // Use the email and userId to populate fields or make requests
            Log.d("CreateGroupActivity", "Logged-in email: " + email);
        } else {
            // Handle missing data (e.g., redirect to login)
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Optionally finish this activity
        }

        fetchSongData();

        // Start WebSocket connection
        if (userEmail != null) {
            // You can now use the userEmail and songId to form the WebSocket URL
            // Assuming you are starting the WebSocket connection after song data is fetched or on a song click.
            startWebSocket(userEmail, songId);
        }


        // Initialize WebSocketManager
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.setWebSocketListener(this); // Set the listener for WebSocket events
        // Connect WebSocket here (now with the correct userEmail and songId)
        webSocketManager.connectWebSocket("ws://coms-3090-048.class.las.iastate.edu:8080/rate/" + userEmail + "/" + songId);


        // Initialize buttons and RecyclerView
        homeButton = findViewById(R.id.navigation_home);
        addUserButton = findViewById(R.id.navigation_adduser);
        messageButton = findViewById(R.id.navigation_message);
        userButton = findViewById(R.id.navigation_user);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        ratingAdapter = new RatingAdapter(this, songList, this, this); // Pass this as the listener for both song clicks and rating changes
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ratingAdapter);

        // Button listeners (navigation)
        homeButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, MainPage.class)));
        addUserButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, CreateGroupActivity.class)));
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

    // WebSocketListener methods
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connected to WebSocket.");
        // Additional operations (e.g., notify user of connection, etc.)
        Toast.makeText(this, "WebSocket Connection Opened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWebSocketMessage(String message) {
        try {
            // Parse the WebSocket message
            JSONArray jsonArray = new JSONArray(message);
            JSONObject jsonMessage = jsonArray.getJSONObject(0);

            int songId = jsonMessage.getInt("songId");
            float newAverageRating = (float) jsonMessage.getDouble("averageRating");

            // Update the song's average rating
            Song updatedSong = songMap.get(String.valueOf(songId));
            if (updatedSong != null) {
                updatedSong.setAverageRating(newAverageRating);
                // Notify the adapter that the song's rating has changed
                ratingAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            Log.e("WebSocket", "Error processing WebSocket message", e);
        }
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Connection closed: " + reason);
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error in WebSocket communication", ex);
    }

    private void startWebSocket(String userEmail, int songId) {
        URI uri = URI.create("ws://coms-3090-048.class.las.iastate.edu:8080/rate/" + userEmail + "/" + songId);

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("WebSocket", "Connected to WebSocket.");
                runOnUiThread(() -> Toast.makeText(MainPage.this, "WebSocket Connection Opened", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onMessage(String message) {
                Log.d("WebSocket", "Received message: " + message);
                runOnUiThread(() -> updateAverageRatingUI(message));  // Update UI with new rating
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("WebSocket", "Connection closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.e("WebSocket", "Error in WebSocket communication", ex);
            }
        };
        // Connect to the WebSocket
        webSocketClient.connect();
    }


    private void updateAverageRatingUI(String message) {
        try {
            // Parse the WebSocket message as JSON
            JSONObject jsonMessage = new JSONObject(message);

            // Extract songId and new average rating from the message
            int songId = jsonMessage.getInt("songId");
            float newAverageRating = (float) jsonMessage.getDouble("averageRating");

            // Find the song in the list and update its average rating
            Song updatedSong = songMap.get(String.valueOf(songId));
            if (updatedSong != null) {
                updatedSong.setAverageRating(newAverageRating);
                // Notify the adapter to refresh the view for the updated song
                ratingAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.e("WebSocket", "Error parsing WebSocket message", e);
        }
    }


    @Override
    public void onSongClick(int songId) {
        Song clickedSong = songMap.get(String.valueOf(songId));
        if (clickedSong != null) {
            // Handle the song click, e.g., show a toast or navigate to a new screen
            Toast.makeText(this, "Song clicked: " + clickedSong.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRatingChanged(int songId, float rating) {
        // Create a JSONArray to send the rating data
        JSONArray ratingData = new JSONArray();

        try {
            // Create a JSONObject with the songId and rating
            JSONObject ratingObject = new JSONObject();
            ratingObject.put("songId", songId);
            ratingObject.put("rating", rating);

            // Add the JSONObject to the JSONArray
            ratingData.put(ratingObject);

            // Send the data via WebSocket
            if (mWebSocket != null && mWebSocket.isOpen()) {
                mWebSocket.send(ratingData.toString());
            } else {
                Log.e("WebSocket", "WebSocket is not connected.");
            }

        } catch (JSONException e) {
            Log.e("WebSocket", "Error creating rating JSON data", e);
        }
    }

}