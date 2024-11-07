package com.example.musibox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private Context context;
    private List<Song> songList;
    private OnSongClickListener onSongClickListener;
    private OnRatingChangeListener onRatingChangeListener;

    // Constructor with the listener for song clicks and rating changes
    public RatingAdapter(Context context, List<Song> songList, OnSongClickListener songClickListener, OnRatingChangeListener ratingChangeListener) {
        this.context = context;
        this.songList = songList;
        this.onSongClickListener = songClickListener;
        this.onRatingChangeListener = ratingChangeListener;
    }

    // Create view holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_rating_view, parent, false);
        return new ViewHolder(view);
    }

    // Bind view holder with data
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = songList.get(position);
        float averageRating = song.getAverageRating();
        holder.titleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtist());
        holder.ratingBar.setRating(song.getAverageRating());
        holder.averageRatingTextView.setText(String.format("Average Rating: %.1f", averageRating));
        // Set the current rating for the RatingBar (could be the average rating)
        holder.ratingBar.setRating(song.getAverageRating());



// Set the listener for when the user changes the rating
        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (onRatingChangeListener != null) {
                onRatingChangeListener.onRatingChanged(song.getId(), rating); // Pass the song ID and new rating
            }
        });

        // Set the click listener for song selection
        holder.itemView.setOnClickListener(v -> {
            if (onSongClickListener != null) {
                onSongClickListener.onSongClick(song.getId()); // Pass the song ID to the listener
            }
        });

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    // Update the song list
    public void updateSongList(List<Song> updatedList) {
        songList = updatedList;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, artistTextView,averageRatingTextView;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.album_name);
            artistTextView = itemView.findViewById(R.id.artist_name);
            averageRatingTextView = itemView.findViewById(R.id.average_rating);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }

    // Define OnSongClickListener interface for song clicks
    public interface OnSongClickListener {
        void onSongClick(int songId);
    }

    // Define OnRatingChangeListener interface for rating changes
    public interface OnRatingChangeListener {
        void onRatingChanged(int songId, float rating);
    }


}