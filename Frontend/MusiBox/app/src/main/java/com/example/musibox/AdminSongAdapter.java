package com.example.musibox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AdminSongAdapter extends RecyclerView.Adapter<AdminSongAdapter.AdminSongViewHolder> {
    private final List<Song> songList;
    private final OnSongClickListener listener;

    // Define an interface for click events
    public interface OnSongClickListener {
        void onSongDelete(Song song); // Handle song delete action
    }

    public AdminSongAdapter(List<Song> songList, OnSongClickListener listener) {
        this.songList = songList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list, parent, false);
        return new AdminSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminSongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.bind(song, listener);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class AdminSongViewHolder extends RecyclerView.ViewHolder {
        private final TextView songTitleView;
        private final TextView songArtistView;
        private final ImageView optionsIcon;
        private final ImageView songCoverView;

        public AdminSongViewHolder(View itemView) {
            super(itemView);
            songTitleView = itemView.findViewById(R.id.songTitle);
            songArtistView = itemView.findViewById(R.id.artist_name);
            optionsIcon = itemView.findViewById(R.id.optionsIcon);
            songCoverView = itemView.findViewById(R.id.albumArt);
        }

        public void bind(Song song, OnSongClickListener listener) {
            songTitleView.setText(song.getTitle());
            songArtistView.setText(song.getArtist());

            // Load the album cover image
            if (song.getCoverUrl() != null && !song.getCoverUrl().isEmpty()) {
                new Thread(() -> {
                    Bitmap bitmap = getBitmapFromURL(song.getCoverUrl());
                    if (bitmap != null) {
                        songCoverView.post(() -> songCoverView.setImageBitmap(bitmap));
                    } else {
                        songCoverView.post(() -> songCoverView.setImageResource(R.drawable.cover)); // Default image
                    }
                }).start();
            } else {
                songCoverView.setImageResource(R.drawable.cover); // Default image
            }

            optionsIcon.setOnClickListener(v -> {
                // Show a popup menu with options like "Delete" and "Add to Playlist"
                PopupMenu popupMenu = new PopupMenu(v.getContext(), optionsIcon);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.song_dropdown, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.delete_song) {
                        listener.onSongDelete(song); // Trigger the delete callback
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
        }

        // Helper method to load images from a URL
        private Bitmap getBitmapFromURL(String src) {
            try {
                Log.d("AdminSongAdapter", "Loading image from URL: " + src);
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
}