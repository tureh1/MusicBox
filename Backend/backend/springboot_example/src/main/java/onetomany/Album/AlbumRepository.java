package onetomany.Album;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> { // Changed from String to Integer
    // Additional custom query methods (if needed) can be added here
}
