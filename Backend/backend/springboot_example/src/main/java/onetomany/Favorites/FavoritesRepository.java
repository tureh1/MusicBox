/*
package onetomany.Favorites;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {


    List<Favorites> findByUserId(int userId);

    List<Favorites> findBySongId(int songId);

    void deleteByUserIdAndSongId(int userId, int songId);

    Optional<Favorites> findById(Long favoritesId);
}*/
