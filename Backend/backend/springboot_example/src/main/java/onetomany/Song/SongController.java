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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    @Autowired
    private DeezerService deezerService;

    @Service
    public class ScheduledTasks {

        @Autowired
        private DeezerService deezerService;

        @Scheduled(cron = "0 0 * * * *") // Run every hour
        public void fetchChartSongsPeriodically() {
            deezerService.fetchAndSaveChartSongs();
        }
    }

    // Endpoint to fetch and save 100 random songs
    @PostMapping("/fetch-random")
    public ResponseEntity<String> fetchRandomSongs() {
        try {
            deezerService.fetchAndSaveRandomSongs();
            return ResponseEntity.ok("Random songs fetched and saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch random songs: " + e.getMessage());
        }
    }

    @PostMapping("/fetch-charts")
    public ResponseEntity<String> fetchChartSongs() {
        try {
            deezerService.fetchAndSaveChartSongs();
            return ResponseEntity.ok("Chart songs fetched and saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch chart songs: " + e.getMessage());
        }
    }

    // Endpoint to fetch a single song by Deezer track ID
    @GetMapping("/fetch/{deezerTrackId}")
    public ResponseEntity<Song> fetchSingleSong(@PathVariable int deezerTrackId) {
        try {
            Song song = deezerService.fetchSongFromDeezer(deezerTrackId);
            if (song != null) {
                return ResponseEntity.ok(song);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    // Fetch and save a song from Deezer
    @PostMapping("/fetch-from-deezer/{deezerTrackId}")
    @Operation(summary = "Fetch a song from Deezer by track ID")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Song fetched and saved successfully"),
            @ApiResponse(responseCode = "409", description = "Song with the same title and artist already exists"),
            @ApiResponse(responseCode = "500", description = "Failed to fetch song from Deezer")
    })
    public ResponseEntity<String> fetchAndSaveSongFromDeezer(@PathVariable int deezerTrackId) {
        Song song = deezerService.fetchSongFromDeezer(deezerTrackId);
        if (song != null) {
            if (songRepository.findByTitleAndArtist(song.getTitle(), song.getArtist()).isPresent()) {
                return ResponseEntity.status(409).body("{\"error\": \"A song with the same title and artist already exists.\"}");
            }
            songRepository.save(song);
            return ResponseEntity.status(201).body("{\"message\": \"Song fetched and saved successfully.\"}");
        }
        return ResponseEntity.status(500).body("{\"error\": \"Failed to fetch song from Deezer.\"}");
    }

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
            summary = "Delete a song",
            description = "Deletes a song from the database by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Song deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Song not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    public ResponseEntity<Map<String, String>> deleteSong(@PathVariable int songId) {
        Optional<Song> songOptional = songRepository.findById(songId);
        if (songOptional.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Song with ID " + songId + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        songRepository.deleteById(songId);
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "Song with ID " + songId + " deleted successfully.");
        return ResponseEntity.ok(successResponse);
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