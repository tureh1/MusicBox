package com.example.musibox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AddPlaylistAdapter extends RecyclerView.Adapter<AddPlaylistAdapter.SongViewHolder> {

    private List<Song> songList;
    private Context context;
    private OnSongAddListener onSongAddListener; 

    // Constructor with listener
    public AddPlaylistAdapter(List<Song> songList, Context context, OnSongAddListener onSongAddListener) {
        this.songList = songList;
        this.context = context;
        this.onSongAddListener = onSongAddListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each song item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_song_item_view, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);

        // Set the song name and artist name
        holder.songNameTextView.setText(song.getTitle());
        holder.artistNameTextView.setText(song.getArtist());

        // Handle the add button click
        holder.addButton.setOnClickListener(v -> {
            if (onSongAddListener != null) {
                notifyItemInserted(songList.size() - 1);
                onSongAddListener.onSongAdd(song); // Add song to playlist
            }
        });

        // Load the album cover
        if (song.getCoverUrl() != null && !song.getCoverUrl().isEmpty()) {
            new Thread(() -> {
                Bitmap bitmap = getBitmapFromURL(song.getCoverUrl());
                holder.albumCoverImageView.post(() -> holder.albumCoverImageView.setImageBitmap(bitmap));
            }).start();
        } else {
            holder.albumCoverImageView.setImageResource(R.drawable.anri); // Default cover
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songNameTextView;
        TextView artistNameTextView;
        ImageView albumCoverImageView;
        ImageButton addButton;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songNameTextView = itemView.findViewById(R.id.songNameTextView);
            artistNameTextView = itemView.findViewById(R.id.artistNameTextView);
            albumCoverImageView = itemView.findViewById(R.id.songImageView);
            addButton = itemView.findViewById(R.id.addsongButton);
        }
    }

    // Interface to handle song add action
    public interface OnSongAddListener {
        void onSongAdd(Song song);
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
