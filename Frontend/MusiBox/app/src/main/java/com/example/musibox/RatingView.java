package com.example.musibox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class RatingView extends LinearLayout {

    private ImageView albumCoverImageView;
    private TextView albumNameTextView;
    private TextView artistNameTextView;
    private TextView releaseDateTextView;
    private TextView averageRatingTextView;
    private RatingBar userRatingBar;

    public RatingView(Context context) {
        super(context);
        initialize(context);
    }

    // Inflate the layout and initialize views
    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_rating_view, this, true);

        albumCoverImageView = view.findViewById(R.id.album_cover);
        albumNameTextView = view.findViewById(R.id.album_name);
        artistNameTextView = view.findViewById(R.id.artist_name);
        releaseDateTextView = view.findViewById(R.id.release_date);
        averageRatingTextView = view.findViewById(R.id.average_rating);
        userRatingBar = view.findViewById(R.id.rating_bar);

        // Set up listener for userRatingBar

        userRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    // Handle user rating without changing the average rating display
                    // Save the user rating or send it to the API if needed
                    System.out.println("User rating updated to: " + rating);
                }
            }
        });
    }

    // Method to bind album data to the views
    public void bindAlbumData(String albumCoverUrl, String albumName, String artistName, String releaseDate, float averageRating) {
        // Use an image loading library (like Glide or Picasso) to load the album cover from a URL
        // Example: Glide.with(getContext()).load(albumCoverUrl).into(albumCoverImageView);
        albumNameTextView.setText(albumName);
        artistNameTextView.setText(artistName);
        releaseDateTextView.setText(releaseDate);

        // Display average rating in the average rating TextView
        averageRatingTextView.setText("Avg: " + averageRating);

        // Set the initial user rating to 0 or a default value (not affecting the average rating)
        userRatingBar.setRating(0);
    }

    // Method to handle the user rating
    private void updateUserRating(float rating) {
        // Update average rating or save to database
        // For example, display the user's rating in the average rating TextView
        // Optionally, we can implement logic to send this rating to  API or database
        System.out.println("User rating updated to: " + rating);
    }
}
