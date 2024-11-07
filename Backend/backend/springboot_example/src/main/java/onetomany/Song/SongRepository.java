package onetomany.Song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Integer> {
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM Song s JOIN s.userRatings r WHERE s.id = :songId AND KEY(r) = :userEmail")
    boolean userHasRatedSong(Long songId, String userEmail);
    Optional<Song> findByTitleAndArtist(String title, String artist);

    // Method to find songs ordered by average rating in descending order
    @Query("SELECT s FROM Song s ORDER BY s.averageRating DESC")
    List<Song> findTopRatedSongs();

    // Method to find songs in random order
    @Query("SELECT s FROM Song s ORDER BY RAND()")
    List<Song> findSongsInRandomOrder();

    List<Song> findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(String title, String artist);
}
