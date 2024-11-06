package com.example.musibox;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private List<Song> songs; // List to hold Song objects

    public RatingAdapter(List<Song> songs) { // Constructor to accept Song list
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RatingView ratingView = new RatingView(parent.getContext());
        return new ViewHolder(ratingView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position); // Get the Song object at the current position
        holder.ratingView.bindSongData(song); // Bind the Song data to the RatingView
    }

    @Override
    public int getItemCount() {
        return songs.size(); // Return the size of the Song list
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RatingView ratingView;

        ViewHolder(RatingView view) {
            super(view);
            ratingView = view;
        }
    }
}
