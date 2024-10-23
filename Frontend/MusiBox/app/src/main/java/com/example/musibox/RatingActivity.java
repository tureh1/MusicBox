package com.example.musibox;

import android.os.Bundle;
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
import java.util.List;

public class RatingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RatingAdapter ratingAdapter;
    private List<Album> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_page);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        albumList = new ArrayList<>();
        ratingAdapter = new RatingAdapter(albumList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ratingAdapter);

        // Fetch album data (dummy data for now)
        fetchAlbumData();
    }

    private void fetchAlbumData() {
        addDummyAlbums();
//        String url = "https://musicbrainz.org/ws/2";
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray releases = response.getJSONArray("releases");
//                            if (releases.length() > 0) {
//                                JSONObject albumObject = releases.getJSONObject(0);
//                                String albumName = albumObject.getString("title");
//                                String artistName = albumObject.getJSONArray("artist-credit").getJSONObject(0).getString("name");
//                                String releaseDate = albumObject.optString("release-date", "N/A");
//                                String releaseId = albumObject.getString("id");
//
//                                // Fetch the cover art
//                                String coverArtUrl = "https://coverartarchive.org/release/" + releaseId + "/front";
//
//                                // Add the album to the list
//                                albumList.add(new Album(coverArtUrl, albumName, artistName, releaseDate, 4.0f)); // Placeholder for averageRating
//
//                                // Notify the adapter that data has changed
//                                ratingAdapter.notifyDataSetChanged();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(RatingActivity.this, "Error parsing album data", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(RatingActivity.this, "Error fetching album data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//
//        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    // Method to add dummy album data
    private void addDummyAlbums() {
        // Adding dummy albums
        albumList.add(new Album("https://link_to_cover_image1.jpg", "Album One", "Artist One", "2024-01-01", 4.2f));
        albumList.add(new Album("https://link_to_cover_image2.jpg", "Album Two", "Artist Two", "2024-02-01", 3.8f));
        albumList.add(new Album("https://link_to_cover_image3.jpg", "Album Three", "Artist Three", "2024-03-01", 4.5f));
        albumList.add(new Album("https://link_to_cover_image4.jpg", "Album Four", "Artist Four", "2024-04-01", 4.0f));
        albumList.add(new Album("https://link_to_cover_image5.jpg", "Album Five", "Artist Five", "2024-05-01", 4.3f));

        // Notify the adapter of the data change
        ratingAdapter.notifyDataSetChanged();
    }
}

