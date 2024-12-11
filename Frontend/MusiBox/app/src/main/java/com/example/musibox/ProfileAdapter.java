package com.example.musibox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Song> songList;
    private Context context;

    public ProfileAdapter(List<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_view, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        // Get the current song
        Song song = songList.get(position);

        // Bind the song data to the view
        holder.songNameTextView.setText(song.getTitle());
        holder.artistNameTextView.setText(song.getArtist());

        // Load the album cover if available
        if (song.getCoverUrl() != null && !song.getCoverUrl().isEmpty()) {
            // Load the album cover in a background thread to avoid blocking the UI
            new Thread(() -> {
                Bitmap bitmap = getBitmapFromURL(song.getCoverUrl());
                holder.albumCoverImageView.post(() -> holder.albumCoverImageView.setImageBitmap(bitmap));
            }).start();
        } else {
            // Set a default image if no album cover URL is provided
            holder.albumCoverImageView.setImageResource(R.drawable.anri); // Default image
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    // ViewHolder class to hold the views for each item in the RecyclerView
    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView songNameTextView;
        TextView artistNameTextView;
        ImageView albumCoverImageView;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            songNameTextView = itemView.findViewById(R.id.songNameTextView);
            artistNameTextView = itemView.findViewById(R.id.artistNameTextView);
            albumCoverImageView = itemView.findViewById(R.id.songImageView);
        }
    }

    // Helper method to load an image from a URL
    private Bitmap getBitmapFromURL(String src) {
        try {
            // Open a connection to the image URL
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);  // Return the Bitmap
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Return null if there's an error loading the image
        }
    }
}
