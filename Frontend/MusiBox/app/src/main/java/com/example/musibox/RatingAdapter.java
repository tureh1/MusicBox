package com.example.musibox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private Context context;
    private List<Song> songList;
    private OnSongClickListener onSongClickListener;

    // Constructor with the listener
    public RatingAdapter(Context context, List<Song> songList, OnSongClickListener listener) {
        this.context = context;
        this.songList = songList;
        this.onSongClickListener = listener;
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
        holder.titleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtist());

        // Set the click listener
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
        TextView titleTextView, artistTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.album_name);
            artistTextView = itemView.findViewById(R.id.artist_name);
        }
    }

    // Define OnSongClickListener interface
    public interface OnSongClickListener {
        void onSongClick(int songId);
    }
}
