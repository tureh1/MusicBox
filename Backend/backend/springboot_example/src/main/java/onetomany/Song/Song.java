package onetomany.Song;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "songs")
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String title;

    @Column
    private String artist;

    @Column
    private double averageRating;

    private String cover;  // New field for album cover URL

    // Getters and setters

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @ElementCollection
    @MapKeyColumn(name = "userEmail")
    @Column(name = "rating")
    private Map<String, Integer> userRatings = new HashMap<>();

    public Song() {}

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
        this.averageRating = 0.0;
    }

    // Add rating for a song and update the average
    public void addRating(int rating, String userEmail) {
        userRatings.put(userEmail, rating);
        updateAverageRating();
    }

    private void updateAverageRating() {
        this.averageRating = userRatings.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    // Check if a user has rated the song
    public boolean userHasRated(String userEmail) {
        return userRatings.containsKey(userEmail);
    }
}