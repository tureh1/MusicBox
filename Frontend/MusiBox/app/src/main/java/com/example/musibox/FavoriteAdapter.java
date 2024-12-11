package com.example.musibox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private final List<Song> favoriteSongs;
    private final Context context;

    public FavoriteAdapter(Context context, List<Song> favoriteSongs) {
        this.context = context;
        this.favoriteSongs = favoriteSongs;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_view, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Song song = favoriteSongs.get(position);

        holder.titleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtist());

        // Load the album cover
        if (song.getCoverUrl() != null && !song.getCoverUrl().isEmpty()) {
            new Thread(() -> {
                Bitmap bitmap = getBitmapFromURL(song.getCoverUrl());
                holder.coverImageView.post(() -> holder.coverImageView.setImageBitmap(bitmap));
            }).start();
        } else {
            holder.coverImageView.setImageResource(R.drawable.anri); // Replace with your placeholder image
        }

// Handle remove button click
        holder.removeButton.setOnClickListener(v -> {
            // Call the removeSongFromBackend method from the activity to remove the song from the backend
            if (context instanceof FavoriteActivity) {
                FavoriteActivity activity = (FavoriteActivity) context;
                activity.removeSongFromBackend(activity.userId, song.getId()); // Pass userId and songId
            }

            // Remove song locally and update UI
            favoriteSongs.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favoriteSongs.size());
        });
    }

    @Override
    public int getItemCount() {
        return favoriteSongs.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        ImageView coverImageView;
        Button removeButton;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.songNameTextView);
            artistTextView = itemView.findViewById(R.id.artistNameTextView);
            coverImageView = itemView.findViewById(R.id.songImageView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
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
