package onetomany.Rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByAlbumId(int albumId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Rating r WHERE r.albumId = :albumId")
    double getAverageRating(int albumId);
}
