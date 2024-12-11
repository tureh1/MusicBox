package com.example.musibox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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

/**
 * The MainPage class represents the main activity in the MusiBox application.
 * It includes functionality for managing songs, fetching data, and updating
 * ratings via WebSocket and REST API calls.
 */
public class MainPage extends AppCompatActivity implements WebSocketListener, RatingAdapter.OnRatingChangeListener {

    private ImageButton homeButton, addUserButton, messageButton, userButton,mainMenu;
    private RecyclerView recyclerView;
    private RatingAdapter ratingAdapter;
    private List<Song> songList = new ArrayList<>();
    private Map<String, Song> songMap = new HashMap<>();
    private WebSocketClient webSocketClient;
    private String userEmail;
    private EditText search_barAlbum;

    /**
     * Called when the activity is first created. Sets up UI components,
     * initializes WebSocket connection, and fetches song data.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains the most recent data.
     */
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
        mainMenu = findViewById(R.id.options_menu);

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

        // Set up the ImageButton to show the menu
        mainMenu.setOnClickListener(view -> showPopupMenu(view));


        search_barAlbum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString();
                if (query.length() >= 1) {
                    searchForSong(query);  // Only search when query length is > 1
                } else {
                    fetchSongData();       // Fetch all songs if query is empty
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

    /**
     * Displays a PopupMenu when the options menu button is clicked.
     *
     * @param anchorView The view to anchor the menu to.
     */
    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.trivia) {
                    Toast.makeText(getApplicationContext(), "Trivia selected", Toast.LENGTH_SHORT).show();
                    Intent triviaIntent = new Intent(getApplicationContext(), TriviaActivity.class);
                    startActivity(triviaIntent); // Start the trivia activity
                    return true;
                } else if (itemId == R.id.playlist) {
                    Toast.makeText(getApplicationContext(), "Favorites selected", Toast.LENGTH_SHORT).show();
                    Intent favoriteIntent = new Intent(getApplicationContext(), FavoriteActivity.class);
                    startActivity(favoriteIntent); // Start the trivia activity
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }


    /**
     * Initializes the WebSocket connection for real-time updates.
     */
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
                            fetchSongData();
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

    /**
     * Searches for songs based on the user's query and updates the song list.
     *
     * @param query The search query entered by the user.
     */
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
                            String coverUrl = songObject.optString("cover", "");
                            String title = songObject.optString("title", "Unknown Song");
                            String artist = songObject.optString("artist", "Unknown Artist");
                            double avgRating = songObject.optDouble("averageRating", 0.0);

                            Song song = new Song(songId, title, artist, avgRating,coverUrl);
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

    /**
     * Updates the UI with the average rating for a song when a new rating is received.
     *
     * @param jsonMessage The JSON object containing the song ID and the updated average rating.
     */
    private void updateAverageRatingUI(JSONObject jsonMessage) {
        try {
            int songId = jsonMessage.getInt("songId");
            double newAverageRating = jsonMessage.getDouble("averageRating");
            fetchSongData();
            Log.d("WebSocket", "Updating song ID: " + songId + " with new rating: " + newAverageRating);

            for (Song song : songList) {
                if (song.getId() == songId) {
                    song.setAverageRating(newAverageRating); // Update the song's rating
                    break;
                }
            }

            ratingAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the UI
        } catch (JSONException e) {
            Log.e("WebSocket", "Error updating rating UI", e);
        }
    }

    /**
     * Fetches all songs from the server and updates the song list.
     */
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
                            String coverUrl = songObject.optString("cover", "");
                            String title = songObject.optString("title", "Unknown Song");
                            String artist = songObject.optString("artist", "Unknown Artist");
                            double avgRating = songObject.optDouble("averageRating", 0.0);

                            Song song = new Song(songId, title, artist, avgRating,coverUrl);
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

    /**
     * Handles changes to the rating of a song and sends the updated rating to the server via WebSocket.
     *
     * @param songId The ID of the song being rated.
     * @param rating The new rating given by the user.
     */
    @Override
    public void onRatingChanged(int songId, int rating) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            try {
                JSONObject ratingUpdate = new JSONObject();
                ratingUpdate.put("songId", songId);
                ratingUpdate.put("rating", rating);
                webSocketClient.send(ratingUpdate.toString());
                fetchSongData(); // Fetch data to refresh the list
            } catch (JSONException e) {
                Log.e("WebSocket", "Failed to send rating update", e);
            }
        }
    }

    /**
     * Closes the WebSocket connection when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close(); // Close WebSocket when the activity is destroyed
        }
    }

    /**
     * Called when the WebSocket connection is successfully opened.
     *
     * @param handshakedata The handshake data received from the server.
     */
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        // Called when the WebSocket connection is opened
        Log.d("WebSocket", "Connection opened: " + handshakedata);
        fetchSongData(); // Fetch data when the WebSocket connection is opened
    }

    /**
     * Called when a message is received from the WebSocket server.
     *
     * @param message The message received as a string.
     */
    @Override
    public void onWebSocketMessage(String message) {
        // Called when a message is received from the server
        runOnUiThread(() -> {
            try {
                // Assuming the message is a JSON string that includes song rating info
                JSONObject jsonMessage = new JSONObject(message);
                updateAverageRatingUI(jsonMessage);  // Custom method to update the UI
                fetchSongData(); // Fetch data to refresh the list
            } catch (JSONException e) {
                Log.e("WebSocket", "Error parsing WebSocket message", e);
            }
        });
    }

    /**
     * Called when the WebSocket connection is closed.
     *
     * @param code   The status code of the closure.
     * @param reason The reason for the closure.
     * @param remote Whether the connection was closed remotely.
     */
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        // Called when the WebSocket connection is closed
        Log.d("WebSocket", "Connection closed: Code = " + code + ", Reason = " + reason);
        Toast.makeText(MainPage.this, "WebSocket closed: " + reason, Toast.LENGTH_SHORT).show();

        // Optional: Reconnect logic if you want to try reconnecting
        // initializeWebSocket(); // Uncomment if you want to automatically reconnect
    }

    /**
     * Called when an error occurs with the WebSocket connection.
     *
     * @param ex The exception representing the error.
     */
    @Override
    public void onWebSocketError(Exception ex) {
        // Called when there's an error with the WebSocket connection
        Log.e("WebSocket", "Error: ", ex);
        Toast.makeText(MainPage.this, "WebSocket Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
    }

}