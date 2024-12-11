package onetomany.Favorites;

import onetomany.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {

    // Find favorites by user ID
    Optional<Favorites> findByUserId(int userId);

    // Find favorites by song ID (via many-to-many relationship)
    List<Favorites> findBySongs_Id(int songId);

    // Delete a song from a user's favorites
    void deleteByUser_IdAndSongs_Id(int userId, int songId);

    Optional<Favorites> findByUser(User user);
}