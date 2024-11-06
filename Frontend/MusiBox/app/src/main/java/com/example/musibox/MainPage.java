package com.example.musibox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import android.widget.ImageButton;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPage extends AppCompatActivity implements WebSocketListener {
    private ImageButton homeButton;
    private ImageButton addUserButton;
    private ImageButton messageButton;
    private ImageButton userButton;
    private RecyclerView recyclerView;
    private RatingAdapter ratingAdapter;
    private List<Song> songList = new ArrayList<>();
    private Map<String, Song> songMap = new HashMap<>(); // Add this line
    // Declare songList
    private String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        fetchUserEmail(); // Fetch the user email from the backend

        homeButton = findViewById(R.id.navigation_home);
        addUserButton = findViewById(R.id.navigation_adduser);
        messageButton = findViewById(R.id.navigation_message);
        userButton = findViewById(R.id.navigation_user);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        songList = new ArrayList<>();
        ratingAdapter = new RatingAdapter(songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ratingAdapter);

        // Initialize WebSocket connection (will be set up after userEmail is retrieved)
        // You can set a placeholder URL until userEmail is ready
        WebSocketManager.getInstance().setWebSocketListener(MainPage.this);

        fetchSongData(); // Fetch songs from the backend

        // Navigation button listeners
        homeButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, MainPage.class)));
        addUserButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, FriendsActivity.class)));
        messageButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, MessageActivity.class)));
        userButton.setOnClickListener(v -> startActivity(new Intent(MainPage.this, UserProfileActivity.class)));
    }

    private void fetchUserEmail() {
        String url = "http://coms-3090-048.class.las.iastate.edu/users/"; // endpoint to fetch user email

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            userEmail = response.getString("email"); // Assuming the response has a field "email"
                            Log.d("MainPage", "User email: " + userEmail);

                            // Initialize WebSocket after email is fetched
                            String serverUrl = "ws://coms-3090-048.class.las.iastate.edu:8080/rate/" + userEmail + "/{songId}";
                            WebSocketManager.getInstance().connectWebSocket(serverUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainPage.this, "Error parsing user email", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainPage.this, "Error fetching user email: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connection opened");
    }

    @Override
    public void onWebSocketMessage(String message) {
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String songId = jsonMessage.getString("songId");
            float avgRating = (float) jsonMessage.getDouble("averageRating");

            // Update the song directly using songMap
            Song song = songMap.get(songId);
            if (song != null) {
                song.setAverageRating(avgRating);
                runOnUiThread(() -> ratingAdapter.notifyDataSetChanged());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Connection closed: " + reason);
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error: " + ex.getMessage());
    }

    // Call this method to send the user rating to the backend
    private void sendUserRating(String songId, float rating) {
        JSONObject ratingJson = new JSONObject();
        try {
            ratingJson.put("rating", rating);
            ratingJson.put("songId", songId);
            // Assuming you have a method to send the rating via WebSocket
            WebSocketManager.getInstance().sendMessage(ratingJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchSongData() {
        String url = "http://coms-3090-048.class.las.iastate.edu/songs/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray songsArray = response.getJSONArray("songs");
                            for (int i = 0; i < songsArray.length(); i++) {
                                JSONObject songObject = songsArray.getJSONObject(i);
                                String songId = songObject.getString("id");
                                String title = songObject.optString("title", "Unknown Song");
                                String artist = songObject.optString("artist", "Unknown Artist");
                                float avgRating = (float) songObject.optDouble("averageRating", 0);

                                // Add the song to songList and songMap
                                Song song = new Song(songId, title, artist, avgRating);
                                songList.add(song);  // Adding to the list
                                songMap.put(songId, song);  // Adding to the map
                            }

                            // Notify the adapter to refresh the data
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
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
