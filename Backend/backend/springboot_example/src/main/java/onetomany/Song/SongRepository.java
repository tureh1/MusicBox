package onetomany.Song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SongRepository extends JpaRepository<Song, Long> {
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM Song s JOIN s.userRatings r WHERE s.id = :songId AND KEY(r) = :userEmail")
    boolean userHasRatedSong(Long songId, String userEmail);
}
