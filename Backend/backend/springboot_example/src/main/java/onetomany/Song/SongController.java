package onetomany.Song;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private SongRepository songRepo;

    @PostMapping
    public String addSong(@RequestBody Song song) {
        songRepo.save(song);
        return "Song added: " + song.getTitle();
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

}
