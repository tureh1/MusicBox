package com.example.musibox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private List<Song> favoriteSongs;
    private RequestQueue requestQueue;
    private String userEmail;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("emailId", null);
        userId = sharedPreferences.getInt("userId", -1);

        // Back Button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.favRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter
        favoriteSongs = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(this, favoriteSongs);
        recyclerView.setAdapter(favoriteAdapter);

        // Initialize the Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Fetch songs from the backend
        fetchFavoriteSongsFromBackend();
    }

    private void fetchFavoriteSongsFromBackend() {

        String url = "http://coms-3090-048.class.las.iastate.edu:8080/users/" + userId + "/favorites";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        favoriteSongs.clear(); // Clear the list to avoid duplicates
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject songObject = response.getJSONObject(i);
                            int id = songObject.getInt("id");
                            String title = songObject.getString("title");
                            String artist = songObject.getString("artist");
                            String coverUrl = songObject.optString("coverUrl", "");
                            favoriteSongs.add(new Song(id, title, artist, coverUrl));
                        }
                        favoriteAdapter.notifyDataSetChanged(); // Refresh the adapter
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(FavoriteActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(FavoriteActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(jsonArrayRequest);
    }

    public void removeSongFromBackend(int userId, int songId) {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/users/" + userId + "/favorites/songs/" + songId;

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                response -> {
                    // Remove the song from the list and refresh
                    for (int i = 0; i < favoriteSongs.size(); i++) {
                        if (favoriteSongs.get(i).getId() == songId) {
                            favoriteSongs.remove(i);
                            break;
                        }
                    }
                    favoriteAdapter.notifyDataSetChanged();
                    Toast.makeText(FavoriteActivity.this, "Song removed successfully", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(FavoriteActivity.this, "Failed to remove song", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(stringRequest);
    }
}
