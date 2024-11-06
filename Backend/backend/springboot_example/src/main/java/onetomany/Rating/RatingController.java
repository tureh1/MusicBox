package onetomany.Rating;

import onetomany.Song.Song;
import onetomany.Song.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private SongRepository songRepo;

    @Autowired
    private RatingRepository ratingRepo; // Add this line to inject the RatingRepository

    @PostMapping("/rate")
    public String rateSong(@RequestParam Long songId, @RequestParam String userEmail, @RequestParam int rating) {
        Optional<Song> songOpt = songRepo.findById(songId);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();

            // Check if the user has already rated this song
            if (ratingRepo.findByUserEmailAndSongId(userEmail, songId).isPresent()) {
                return "User has already rated this song.";
            } else {
                // Save the new rating
                Rating newRating = new Rating(userEmail, songId, rating);
                ratingRepo.save(newRating);
                song.addRating(rating, userEmail); // Update the song's average rating
                songRepo.save(song); // Save the updated song
                return "Rating added for song: " + song.getTitle();
            }
        }
        return "Song not found";
    }

    @GetMapping("/{songId}/average")
    public String getAverageRating(@PathVariable Long songId) {
        Optional<Song> songOpt = songRepo.findById(songId);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            return "Average rating for " + song.getTitle() + " is " + song.getAverageRating();
        }
        return "Song not found";
    }
}
