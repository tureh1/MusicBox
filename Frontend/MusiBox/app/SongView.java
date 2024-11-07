import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.musibox.R;

public class SongView {
    private TextView songNameTextView;
    private TextView artistNameTextView;
    private TextView averageRatingTextView;
    private RatingBar userRatingBar;

    public SongView(Context context) {
        super(context);
        initialize(context);
    }

    // Inflate the layout and initialize views
    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_playlist, this, true);

        songNameTextView = view.findViewById(R.id.album_name);
        artistNameTextView = view.findViewById(R.id.artist_name);
        averageRatingTextView = view.findViewById(R.id.average_rating);
        userRatingBar = view.findViewById(R.id.rating_bar);

        // Set up listener for userRatingBar
        userRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    // Handle user rating without changing the average rating display
                    System.out.println("User rating updated to: " + rating);
                }
            }
        });
    }

    // Method to bind song data to the views
    public void bindSongData(Song song) {
        // Example: Glide.with(getContext()).load(song.getCoverUrl()).into(albumCoverImageView);
        songNameTextView.setText(song.getTitle());
        artistNameTextView.setText(song.getArtist());

        // Display average rating in the average rating TextView
        averageRatingTextView.setText("Avg: " + song.getAverageRating());

        // Set the initial user rating to 0 or a default value (not affecting the average rating)
        userRatingBar.setRating(0);
    }

    // Method to handle the user rating
    private void updateUserRating(float rating) {
        // Update average rating or save to database
        System.out.println("User rating updated to: " + rating);
    }
}

}
