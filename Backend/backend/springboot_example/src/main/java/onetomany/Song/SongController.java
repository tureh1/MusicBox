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
    public String deleteSong(@PathVariable Long songId) {
        songRepo.deleteById(songId);
        return "Song deleted with ID: " + songId;
    }

    @GetMapping
    public Map<String, List<Song>> getAllSongs() {
        List<Song> songs = songRepo.findAll();
        Map<String, List<Song>> response = new HashMap<>();
        response.put("songs", songs);  // Wrap the song list in a "songs" array
        return response;
    }

    // Retrieve top-rated songs
    @GetMapping("/top-rated")
    public ResponseEntity<List<Song>> getTopRatedSongs() {
        List<Song> topRatedSongs = songRepository.findTopRatedSongs();
        return ResponseEntity.ok(topRatedSongs);
    }

}
