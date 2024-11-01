package onetomany.Rating;

import java.util.Date;
import onetomany.Album.Album;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

//import lombok.Data;

@Entity
@Table(name = "ratings")
public class Rating extends onetomany.Album.Album{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "album_id", nullable = false)
    private int albumId;  // Changed from String to int

    private String userName;
    private int rating;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submitted_at")
    private Date submittedAt = new Date();

    public Rating() {}

    public Rating(int albumId, String userName, int rating) {
        this.albumId = albumId;
        this.userName = userName;
        this.rating = rating;
    }

    // Getters and setters
    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
}
