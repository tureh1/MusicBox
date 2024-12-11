package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private Context context;
    private List<Song> songList;
    private OnRatingChangeListener onRatingChangeListener;

    // Constructor
    public RatingAdapter(Context context, List<Song> songList, OnRatingChangeListener ratingChangeListener) {
        this.context = context;
        this.songList = songList;
        this.onRatingChangeListener = ratingChangeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_rating_view, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Song song = songList.get(position);
        double averageRating = song.getAverageRating();
        holder.titleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtist());
        holder.averageRatingTextView.setText(String.format("Average Rating: %.1f", averageRating));

        // Set the user rating
        holder.ratingBar.setRating(song.getUserRating());

        // Load the album cover
        if (song.getCoverUrl() != null && !song.getCoverUrl().isEmpty()) {
            new Thread(() -> {
                Bitmap bitmap = getBitmapFromURL(song.getCoverUrl());
                holder.albumCoverImageView.post(() -> holder.albumCoverImageView.setImageBitmap(bitmap));
            }).start();
        } else {
            holder.albumCoverImageView.setImageResource(R.drawable.cover);
        }

        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (onRatingChangeListener != null) {
                song.setUserRating(rating);
                onRatingChangeListener.onRatingChanged(song.getId(), (int) rating);
            }
        });

        //Handle HeartButton
        if(song.isHeartFilled()){
            holder.heartButton.setImageResource(R.drawable.baseline_favorite_24); // Filled heart drawable
        } else {
            holder.heartButton.setImageResource(R.drawable.baseline_favorite_border_24); // Empty heart drawable
        }

        holder.heartButton.setOnClickListener(v -> {
            song.setHeartFilled(!song.isHeartFilled()); // Toggle heart state
            notifyItemChanged(position); // Refresh the specific item

            // Retrieve user data from SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("emailId", null); // Default to null if not found
            int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if not found

            // Send the song ID to the backend
            String url = "http://coms-3090-048.class.las.iastate.edu:8080/songs/" + userId + "/favorite/" + song.getId();

            // Create a Volley request to send the song ID
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Handle successful response from the server (optional)
                            Log.d("Volley", "Response: " + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error response (optional)
                            Log.e("Volley", "Error: " + error.getMessage());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Send the song ID as a parameter in the POST request
                    Map<String, String> params = new HashMap<>();
                    params.put("songId", String.valueOf(song.getId()));
                    return params;
                }
            };

            // Add the request to the Volley request queue
            Volley.newRequestQueue(context).add(stringRequest);
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, artistTextView, averageRatingTextView;
        RatingBar ratingBar;
        ImageView albumCoverImageView;
        ImageButton heartButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.album_name);
            artistTextView = itemView.findViewById(R.id.artist_name);
            averageRatingTextView = itemView.findViewById(R.id.average_rating);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            albumCoverImageView = itemView.findViewById(R.id.album_cover);
            heartButton = itemView.findViewById(R.id.heartButton);
        }
    }

    public interface OnRatingChangeListener {
        void onRatingChanged(int songId, int rating);
    }

    // Helper method to load images from a URL
    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
