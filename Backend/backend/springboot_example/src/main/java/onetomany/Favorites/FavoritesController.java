/*
package onetomany.Favorites;

import onetomany.GroupPlaylist.GroupPlaylistRepository;
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

*/
/*    // Create a favorites playlist for a user if it doesn't exist
    @PostMapping
    public ResponseEntity<String> createFavoritesForUser(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (favoritesRepository.findByUserId(userId).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Favorites already exist for this user.");
            }

            Favorites favorites = new Favorites();
            favorites.setUser(user);
            favoritesRepository.save(favorites);
            return ResponseEntity.status(HttpStatus.CREATED).body("Favorites playlist created for user.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }*//*


    // Add a song to favorites
    @PostMapping("/{favoritesId}/songs/{songId}")
    public ResponseEntity<String> addSongToFavorites(
            @PathVariable Long userId,
            @PathVariable Long favoritesId,
            @PathVariable int songId) {

        Optional<Favorites> favoritesOpt = favoritesRepository.findById(favoritesId);
        Optional<Song> songOpt = songRepository.findById(songId);

        if (favoritesOpt.isPresent() && songOpt.isPresent()) {
            Favorites favorites = favoritesOpt.get();
            Song song = songOpt.get();

            if (!favorites.containsSong(song)) {
                favorites.addSong(song);
                favoritesRepository.save(favorites);
                return ResponseEntity.ok("Song added to favorites.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Song is already in favorites.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorites or Song not found.");
    }

    // Remove a song from favorites
    @DeleteMapping("/{favoritesId}/songs/{songId}")
    public ResponseEntity<String> removeSongFromFavorites(
            @PathVariable Long userId,
            @PathVariable Long favoritesId,
            @PathVariable int songId) {

        Optional<Favorites> favoritesOpt = favoritesRepository.findById(favoritesId);
        Optional<Song> songOpt = songRepository.findById(songId);

        if (favoritesOpt.isPresent() && songOpt.isPresent()) {
            Favorites favorites = favoritesOpt.get();
            Song song = songOpt.get();

            if (favorites.containsSong(song)) {
                favorites.removeSong(song);
                favoritesRepository.save(favorites);
                return ResponseEntity.ok("Song removed from favorites.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Song is not in favorites.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorites or Song not found.");
    }

    // Get all songs in favorites
    @GetMapping("/{favoritesId}/songs")
    public ResponseEntity<?> getFavoritesSongs(
            @PathVariable Long userId,
            @PathVariable Long favoritesId) {

        Optional<Favorites> favoritesOpt = favoritesRepository.findById(favoritesId);

        if (favoritesOpt.isPresent()) {
            return ResponseEntity.ok(favoritesOpt.get().getSongs());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorites not found.");
    }
}
*/
