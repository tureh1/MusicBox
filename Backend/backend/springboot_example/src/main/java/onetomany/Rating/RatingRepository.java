package onetomany.Rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    Optional<Rating> findByUserEmailAndSongId(String userEmail, int songId);

    List<Rating> findBySongId(int songId);

    // New query to calculate the average rating for a specific song
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.songId = :songId")
    Double findAverageRatingBySongId(@Param("songId") int songId);
}
