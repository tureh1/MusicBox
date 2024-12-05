package onetomany.Rating;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import onetomany.Song.Song;
import onetomany.Song.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ratings")
public class  RatingController {

    @Autowired
    private SongRepository songRepo;

    @Autowired
    private RatingRepository ratingRepo;

    @PostMapping("/rate")
    @Operation(
            summary = "adding a rating to a song",
            description = "a user rating a song and overwriting if they have previously rated"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully rated song",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public String rateSong(@RequestParam int songId, @RequestParam String userEmail, @RequestParam int rating) {
        Optional<Song> songOpt = songRepo.findById(songId);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();

            // Check if the user has already rated this song
            Optional<Rating> existingRating = ratingRepo.findByUserEmailAndSongId(userEmail, songId);
            if (existingRating.isPresent()) {
                // If a rating exists, update it
                Rating ratingToUpdate = existingRating.get();
                ratingToUpdate.setRating(rating);
                ratingRepo.save(ratingToUpdate);  // Save the updated rating
                song.addRating(rating, userEmail); // Update the song's average rating
                songRepo.save(song); // Save the updated song
                return "Rating updated for song: " + song.getTitle();
            } else {
                // If no rating exists, add a new one
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
    @Operation(
            summary = "get the average rating of a song",
            description = "fetches the aggregate average rating of a song based on live ratings"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully fetched rating",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public String getAverageRating(@PathVariable int songId) {
        Optional<Song> songOpt = songRepo.findById(songId);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            return "Average rating for " + song.getTitle() + " is " + song.getAverageRating();
        }
        return "Song not found";
    }


}
