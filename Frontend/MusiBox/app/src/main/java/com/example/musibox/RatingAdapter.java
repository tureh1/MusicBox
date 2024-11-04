package com.example.musibox;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private List<Album> albums;

    public RatingAdapter(List<Album> albums) {
        this.albums = albums;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RatingView ratingView = new RatingView(parent.getContext());
        return new ViewHolder(ratingView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.ratingView.bindAlbumData(album.getCoverUrl(), album.getName(), album.getArtist(), album.getReleaseDate(), album.getAverageRating());
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RatingView ratingView;

        ViewHolder(RatingView view) {
            super(view);
            ratingView = view;
        }
    }
}

