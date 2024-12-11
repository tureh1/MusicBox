
package com.example.musibox;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musibox.R;
import com.example.musibox.Song;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.SongViewHolder> {

    private List<Song> songList;
    private Context context;

    public PlaylistAdapter(List<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_view, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.songNameTextView.setText(song.getTitle());
        holder.artistNameTextView.setText(song.getArtist());

        // Handle remove button click
        holder.removeButton.setOnClickListener(v -> {
            if (context instanceof PlaylistActivity) {
                ((PlaylistActivity) context).removeSong(
                        ((PlaylistActivity) context).userId,
                        ((PlaylistActivity) context).playlistId,
                        song.getId()
                );
            }
        });


        // Load the album cover
        if (song.getCoverUrl() != null && !song.getCoverUrl().isEmpty()) {
            new Thread(() -> {
                Bitmap bitmap = getBitmapFromURL(song.getCoverUrl());
                holder.albumCoverImageView.post(() -> holder.albumCoverImageView.setImageBitmap(bitmap));
            }).start();
        } else {
            holder.albumCoverImageView.setImageResource(R.drawable.anri);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songNameTextView;
        TextView artistNameTextView;
        Button removeButton;
        ImageView albumCoverImageView;
        ImageButton addButton;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songNameTextView = itemView.findViewById(R.id.songNameTextView);
            artistNameTextView = itemView.findViewById(R.id.artistNameTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
            addButton = itemView.findViewById(R.id.addButton);
            albumCoverImageView = itemView.findViewById(R.id.songImageView);
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

