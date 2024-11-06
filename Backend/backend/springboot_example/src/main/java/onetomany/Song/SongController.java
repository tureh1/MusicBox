package onetomany.Song;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public String deleteSong(@PathVariable Integer songId) {
        songRepo.deleteById(songId);
        return "Song deleted with ID: " + songId;
    }

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        List<Song> songs = songRepo.findAll();
        return ResponseEntity.ok(songs);  // Directly return the list of songs

    }

    // Retrieve top-rated songs
    @GetMapping("/top-rated")
    public ResponseEntity<List<Song>> getTopRatedSongs() {
        List<Song> topRatedSongs = songRepository.findTopRatedSongs();
        return ResponseEntity.ok(topRatedSongs);
    }

    // Retrieve songs in random order
    @GetMapping("/random")
    public ResponseEntity<List<Song>> getSongsInRandomOrder() {
        List<Song> randomSongs = songRepository.findSongsInRandomOrder();
        return ResponseEntity.ok(randomSongs);
    }

}
