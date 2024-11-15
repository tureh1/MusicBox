package onetomany.Rating;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ratings")
@Data
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userEmail; // User's email_changes by tu

    @Column
    private int songId; // ID of the song being rated

    @Column
    private int rating; // Rating value between 1 and 5

    public Rating() {}

    public Rating(String userEmail, int songId, int rating) {
        this.userEmail = userEmail;
        this.songId = songId;
        this.rating = rating;
    }
}
