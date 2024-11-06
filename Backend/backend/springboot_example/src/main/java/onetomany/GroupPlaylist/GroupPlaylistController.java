package onetomany.GroupPlaylist;

import onetomany.Users.User;
import onetomany.Users.UserRepository;
import onetomany.Song.Song;
import onetomany.Song.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/playlists")
public class GroupPlaylistController {

    @Autowired
    private GroupPlaylistRepository playlistRepo;

    @Autowired
    private SongRepository songRepo;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> createPlaylist(@RequestBody CreatePlaylistRequest playlistRequest) {
        List<String> userEmails = playlistRequest.getUsers();

        // Create the playlist object
        GroupPlaylist playlist = new GroupPlaylist();

        // Set the playlist name if provided
        if (playlistRequest.getName() != null && !playlistRequest.getName().trim().isEmpty()) {
            playlist.setName(playlistRequest.getName());
        }

        // Add users to the playlist if provided
        if (userEmails != null && !userEmails.isEmpty()) {
            for (String email : userEmails) {
                Optional<User> userOpt = Optional.ofNullable(userRepository.findByEmailId(email));
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (playlist.hasUser(user)) {
                        return ResponseEntity.ok("{\"message\": \"User already in playlist: " + email + "\"}");
                    }
                    playlist.addUser(user);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found: " + email + "\"}");
                }
            }
        }

        // Save the playlist
        playlistRepo.save(playlist);

        String responseMessage = (playlist.getName() != null && !playlist.getName().isEmpty()) ?
                "{\"message\": \"Playlist '" + playlist.getName() + "' created successfully\"}" :
                "{\"message\": \"Playlist created without a name\"}";

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    // Update the playlist name with a PUT request
    @PutMapping("/{playlistId}/updateName")
    public ResponseEntity<String> updatePlaylistName(@PathVariable Long playlistId, @RequestBody Map<String, String> request) {
        Optional<GroupPlaylist> playlistOpt = playlistRepo.findById(playlistId);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Playlist not found\"}");
        }

        GroupPlaylist playlist = playlistOpt.get();
        String newName = request.get("name");

        if (newName == null || newName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"New name is required\"}");
        }

        playlist.setName(newName);
        playlistRepo.save(playlist);

        return ResponseEntity.ok("{\"message\": \"Playlist name updated to: " + newName + "\"}");
    }

    // Add a user to an existing playlist
    @PostMapping("/{playlistId}/addUser")
    public ResponseEntity<String> addUserToPlaylist(@PathVariable Long playlistId, @RequestBody Map<String, String> userRequest) {
        String userEmail = userRequest.get("userEmail");

        if (userEmail == null || userEmail.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"User email is required\"}");
        }

        Optional<GroupPlaylist> playlistOpt = playlistRepo.findById(playlistId);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Playlist not found\"}");
        }

        GroupPlaylist playlist = playlistOpt.get();
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByEmailId(userEmail));

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Check if the user is already in the playlist
            if (playlist.hasUser(user)) {
                return ResponseEntity.ok("{\"message\": \"User already in playlist: " + userEmail + "\"}");
            }

            // Add user to the playlist
            playlist.addUser(user);
            playlistRepo.save(playlist);
            return ResponseEntity.ok("{\"message\": \"User added to playlist: " + userEmail + "\"}");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found: " + userEmail + "\"}");
        }
    }

    // Get all users in the playlist
    @GetMapping("/{playlistId}/users")
    public ResponseEntity<Set<User>> getUsersInPlaylist(@PathVariable Long playlistId) {
        Optional<GroupPlaylist> playlistOpt = playlistRepo.findById(playlistId);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        GroupPlaylist playlist = playlistOpt.get();
        Set<User> users = playlist.getUsers();
        return ResponseEntity.ok(users);
    }

    // Get all songs in the playlist
    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<Set<Song>> getSongsInPlaylist(@PathVariable Long playlistId) {
        Optional<GroupPlaylist> playlistOpt = playlistRepo.findById(playlistId);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        GroupPlaylist playlist = playlistOpt.get();
        Set<Song> songs = playlist.getSongs();
        return ResponseEntity.ok(songs);
    }

    // Retrieve all group playlists along with their users and songs
    @GetMapping
    public ResponseEntity<List<GroupPlaylist>> getAllPlaylists() {
        List<GroupPlaylist> playlists = playlistRepo.findAll();
        return ResponseEntity.ok(playlists);
    }

    // Get both users and songs in the playlist
    @GetMapping("/{playlistId}")
    public ResponseEntity<Map<String, Object>> getPlaylistDetails(@PathVariable Long playlistId) {
        Optional<GroupPlaylist> playlistOpt = playlistRepo.findById(playlistId);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        GroupPlaylist playlist = playlistOpt.get();
        Set<Song> songs = playlist.getSongs();
        Set<User> users = playlist.getUsers();

        Map<String, Object> details = new HashMap<>();
        details.put("users", users);
        details.put("songs", songs);

        return ResponseEntity.ok(details);
    }

    // Add a song to an existing playlist
    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<String> addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        Optional<GroupPlaylist> playlistOpt = playlistRepo.findById(playlistId);
        Optional<Song> songOpt = songRepo.findById(songId);

        if (playlistOpt.isPresent() && songOpt.isPresent()) {
            GroupPlaylist playlist = playlistOpt.get();
            Song song = songOpt.get();

            // Check if the song is already in the playlist
            if (playlist.getSongs().contains(song)) {
                return ResponseEntity.ok("{\"message\": \"Song already in playlist: " + song.getTitle() + "\"}");
            }

            playlist.addSong(song);
            playlistRepo.save(playlist);
            return ResponseEntity.ok("{\"message\": \"Song added to playlist: " + song.getTitle() + "\"}");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Playlist or Song not found\"}");
    }

    // Remove a song from an existing playlist
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<String> removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        Optional<GroupPlaylist> playlistOpt = playlistRepo.findById(playlistId);
        Optional<Song> songOpt = songRepo.findById(songId);

        if (playlistOpt.isPresent() && songOpt.isPresent()) {
            GroupPlaylist playlist = playlistOpt.get();
            Song song = songOpt.get();

            // Check if the song is in the playlist
            if (!playlist.getSongs().contains(song)) {
                return ResponseEntity.ok("{\"message\": \"Song not in playlist: " + song.getTitle() + "\"}");
            }

            // Remove the song from the playlist
            playlist.getSongs().remove(song);
            playlistRepo.save(playlist);
            return ResponseEntity.ok("{\"message\": \"Song removed from playlist: " + song.getTitle() + "\"}");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Playlist or Song not found\"}");
    }
}
