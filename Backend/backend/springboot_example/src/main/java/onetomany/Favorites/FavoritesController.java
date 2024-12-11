package onetomany.Favorites;

import onetomany.Song.Song;
import onetomany.Song.SongRepository;
import onetomany.Users.User;
import onetomany.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users/{userId}/favorites")
public class FavoritesController {

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all songs in the user's favorites
    @GetMapping
    public ResponseEntity<?> getFavoritesSongs(@PathVariable int userId) {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(userId));

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<Favorites> favoritesOpt = favoritesRepository.findByUser(user);

            if (favoritesOpt.isPresent()) {
                Favorites favorites = favoritesOpt.get();
                return ResponseEntity.ok(favorites.getSongs());
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorites not found for the user.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }

    // Add a song to the user's favorites (POST method)
    @PostMapping("/songs/{songId}")
    public ResponseEntity<String> addSongToFavorites(
            @PathVariable int userId,
            @PathVariable int songId) {

        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(userId));
        Optional<Song> songOpt = songRepository.findById(songId);

        if (userOpt.isPresent() && songOpt.isPresent()) {
            User user = userOpt.get();
            Optional<Favorites> favoritesOpt = favoritesRepository.findByUser(user);

            // Create favorites list if not exists
            Favorites favorites = favoritesOpt.orElseGet(() -> {
                Favorites newFavorites = new Favorites();
                newFavorites.setUser(user);
                return favoritesRepository.save(newFavorites);
            });

            // Check if the song is already in the user's favorites
            Song song = songOpt.get();
            if (!favorites.containsSong(song)) {
                favorites.addSong(song);
                favoritesRepository.save(favorites);
                return ResponseEntity.status(HttpStatus.CREATED).body("Song added to favorites.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Song is already in favorites.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Song not found.");
    }

    // Remove a song from the user's favorites
    @DeleteMapping("/songs/{songId}")
    public ResponseEntity<String> removeSongFromFavorites(
            @PathVariable int userId,
            @PathVariable int songId) {

        Optional<User> userOpt = Optional.ofNullable(userRepository.findById(userId));
        Optional<Song> songOpt = songRepository.findById(songId);

        if (userOpt.isPresent() && songOpt.isPresent()) {
            User user = userOpt.get();
            Optional<Favorites> favoritesOpt = favoritesRepository.findByUser(user);

            if (favoritesOpt.isPresent()) {
                Favorites favorites = favoritesOpt.get();
                Song song = songOpt.get();

                // Check if the song is in the user's favorites
                if (favorites.containsSong(song)) {
                    favorites.removeSong(song);
                    favoritesRepository.save(favorites);
                    return ResponseEntity.ok("Song removed from favorites.");
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Song is not in favorites.");
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorites list not found.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Song not found.");
    }
}
