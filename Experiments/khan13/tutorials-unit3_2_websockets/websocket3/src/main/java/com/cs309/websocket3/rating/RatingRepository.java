package com.cs309.websocket3.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Fetch all ratings for a specific album by its ID
    List<Rating> findByAlbumId(String albumId);

    // Calculate the average rating for an album
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Rating r WHERE r.albumId = :albumId")
    double getAverageRating(String albumId);
}
