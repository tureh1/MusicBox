package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, artistTextView, averageRatingTextView;
        RatingBar ratingBar;
        ImageView albumCoverImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.album_name);
            artistTextView = itemView.findViewById(R.id.artist_name);
            averageRatingTextView = itemView.findViewById(R.id.average_rating);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            albumCoverImageView = itemView.findViewById(R.id.album_cover);
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
