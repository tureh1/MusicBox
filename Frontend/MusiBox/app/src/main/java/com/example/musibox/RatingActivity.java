package com.example.musibox;

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


import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends AppCompatActivity implements WebSocketListener {

    private RecyclerView recyclerView;
    private RatingAdapter ratingAdapter;
    private List<Album> albumList;
    private String albumId; // Define this at the class level



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_page);
        albumId = getIntent().getStringExtra("albumId");

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        albumList = new ArrayList<>();
        ratingAdapter = new RatingAdapter(albumList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ratingAdapter);

        // Initialize WebSocket
        // Establish WebSocket connection and set listener
        String serverUrl = "ws://10.90.72.167:8080/${albumId}/rating";

        WebSocketManager.getInstance().connectWebSocket(serverUrl);
        WebSocketManager.getInstance().setWebSocketListener(RatingActivity.this);


        // Set albumId before calling fetchAlbumData
        albumId = "albumId"; // Replace with actual value
        fetchAlbumData();
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connection opened");
    }

    @Override
    public void onWebSocketMessage(String message) {
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String albumId = jsonMessage.getString("albumId");
            float avgRating = (float) jsonMessage.getDouble("averageRating");

            // Update the albumList and notify the adapter
            for (Album album : albumList) {
                if (album.getAlbumId().equals(albumId)) {
                    album.setAverageRating(avgRating);
                    break;
                }
            }
            runOnUiThread(() -> ratingAdapter.notifyDataSetChanged());

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
    private void sendUserRating(String albumId, float rating) {
        JSONObject ratingJson = new JSONObject();
        try {
            ratingJson.put("albumId", albumId);
            ratingJson.put("rating", rating);
            WebSocketManager.getInstance().sendMessage(ratingJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchAlbumData() {
        String url = "http://10.90.72.167:8080/" + albumId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Assuming the backend provides an array of album ratings under "albums"
                            JSONArray albumsArray = response.getJSONArray("albums");
                            for (int i = 0; i < albumsArray.length(); i++) {
                                JSONObject albumObject = albumsArray.getJSONObject(i);

                                // Retrieve details based on the provided backend structure
                                String albumId = albumObject.getString("albumId");
                                String albumName = albumObject.optString("albumName", "Unknown Album");
                                String artistName = albumObject.optString("artistName", "Unknown Artist");
                                float avgRating = (float) albumObject.optDouble("averageRating", 0); // Placeholder average rating
                                String releaseDate = albumObject.optString("releaseDate", "N/A");
                                String coverArtUrl = "https://coverartarchive.org/release/" + albumId + "/front"; // Assumes albumId is used for cover art URL

                                // Add album to the list using the correct constructor order
                                albumList.add(new Album(coverArtUrl, albumId, albumName, artistName, releaseDate, avgRating));
                            }

                            // Notify the adapter that data has changed to update RecyclerView
                            ratingAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RatingActivity.this, "Error parsing album data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RatingActivity.this, "Error fetching album data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}

    // Method to add dummy album data
//    private void addDummyAlbums() {
//        // Adding dummy albums
//        albumList.add(new Album(
//                "https://lastfm.freetls.fastly.net/i/u/6e0bbbb097073fc4df40be2a5e270797",
//                "1", // ID
//                "The Slow Rush Remixes/B-sides", // Name
//                "Tame Impala",
//                "2020-02-14",
//                4.2f));
//
//        albumList.add(new Album(
//                "https://lastfm.freetls.fastly.net/i/u/d47d0db3893fa94639514a2aa47372b8",
//                "2", // ID
//                "Beatopia", // Name
//                "Beabadoobee",
//                "2022-07-15",
//                3.8f));
//
//        albumList.add(new Album(
//                "https://lastfm.freetls.fastly.net/i/u/e69971625c379772fb79213dccfa194f",
//                "3", // ID
//                "Hit me hard and Soft", // Name
//                "Billie Eilish",
//                "2024-04-17",
//                4.5f));
//
//        albumList.add(new Album(
//                "https://lastfm.freetls.fastly.net/i/u/1edfa1d35ec3cbe08b6f4a569b005807",
//                "4", // ID
//                "...Baby one more time", // Name
//                "Britney Spears",
//                "1999-01-12",
//                4.0f));
//
//        albumList.add(new Album(
//                "https://lastfm.freetls.fastly.net/i/u/579ed8a3dca4a0dc7c055316307a0056",
//                "5", // ID
//                "Top Shotta", // Name
//                "NLE Choppa",
//                "2020-08-07",
//                4.3f));
//        // Notify the adapter of the data change
//        ratingAdapter.notifyDataSetChanged();
//    }


