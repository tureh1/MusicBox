package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private Context context;
    private List<Song> songList;
    private OnRatingChangeListener onRatingChangeListener;

    // Constructor with the listener for song clicks and rating changes
    public RatingAdapter(Context context, List<Song> songList, OnRatingChangeListener ratingChangeListener) {
        this.context = context;
        this.songList = songList;
        this.onRatingChangeListener = ratingChangeListener;
    }

    // Create view holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_rating_view, parent, false);
        return new ViewHolder(view);
    }

    // Bind view holder with data
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = songList.get(position);
        double averageRating = song.getAverageRating();
        holder.titleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtist());
        holder.averageRatingTextView.setText(String.format("Average Rating: %.1f", averageRating));

        // Set the user rating (not the average) to the RatingBar
        holder.ratingBar.setRating(song.getUserRating()); // Bind user rating here

        // Set the listener for when the user changes the rating
        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (onRatingChangeListener != null) {
                // Update the user's rating and pass it to the listener
                song.setUserRating(rating); // Update the Song model with the new user rating
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
        TextView titleTextView, artistTextView, averageRatingTextView;
        RatingBar ratingBar;
        ImageButton heartButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.album_name);
            artistTextView = itemView.findViewById(R.id.artist_name);
            averageRatingTextView = itemView.findViewById(R.id.average_rating);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            heartButton = itemView.findViewById(R.id.heartButton);
        }
    }

    // Define OnRatingChangeListener interface for rating changes
    public interface OnRatingChangeListener {
        void onRatingChanged(int songId, int rating);
    }
}
