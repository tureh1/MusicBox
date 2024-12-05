package onetomany.Song;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private SongRepository songRepo;

    @Autowired
    private SongRepository songRepository;

    // Create a new song
    @PostMapping
    @Operation(
            summary = "creating a song",
            description = "adding a song to our database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added song",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<String> createSong(@RequestBody Song songRequest) {
        // Check if a song with the same title and artist already exists
        if (songRepository.findByTitleAndArtist(songRequest.getTitle(), songRequest.getArtist()).isPresent()) {
            return ResponseEntity.status(409).body("{\"error\": \"A song with the same title and artist already exists.\"}");
        }

        // Save the new song
        songRepository.save(songRequest);
        return ResponseEntity.status(201).body("{\"message\": \"Song created successfully.\"}");
    }

    @DeleteMapping("/{songId}")
    @Operation(
            summary = "deleting a song",
            description = "deleting a song from our database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted song",
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
    public String deleteSong(@PathVariable Integer songId) {
        songRepo.deleteById(songId);
        return "Song deleted with ID: " + songId;
    }

    @GetMapping
    @Operation(
            summary = "get all songs",
            description = "fetches all songs in our database"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully fetched songs",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<Song>> getAllSongs() {
        List<Song> songs = songRepo.findAll();
        return ResponseEntity.ok(songs);  // Directly return the list of songs

    }

    // Retrieve top-rated songs
    @GetMapping("/top-rated")
    @Operation(
            summary = "gets top ratee songs",
            description = "fetches the top rated songs based on average rating"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully fetched songs",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<Song>> getTopRatedSongs() {
        List<Song> topRatedSongs = songRepository.findTopRatedSongs();
        return ResponseEntity.ok(topRatedSongs);
    }

    // Retrieve songs in random order
    @GetMapping("/random")
    @Operation(
            summary = "gets random songs",
            description = "fetches songs in a random order to display on the main page"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully fetched songs",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<Song>> getSongsInRandomOrder() {
        List<Song> randomSongs = songRepository.findSongsInRandomOrder();
        return ResponseEntity.ok(randomSongs);
    }

    //search for a song
    @GetMapping("/search")
    @Operation(
            summary = "search for a song",
            description = "search for a song based on if the the search matched any characters in the title or artist name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully found songs pertaining to search",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<Song>> searchSongs(@RequestParam String query) {
        List<Song> songs = songRepo.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(query, query);
        return ResponseEntity.ok(songs);
    }
}
