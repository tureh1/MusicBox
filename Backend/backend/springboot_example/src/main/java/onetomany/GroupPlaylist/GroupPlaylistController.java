package onetomany.GroupPlaylist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import onetomany.Users.User;
import onetomany.Users.UserRepository;
import onetomany.Song.Song;
import onetomany.Song.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class GroupPlaylistController {

    @Autowired
    private GroupPlaylistRepository playlistRepo;

    @Autowired
    private SongRepository songRepo;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users/{userId}/playlists")
    @Operation(
            summary = "create a group playlist",
            description = "a user creating a group playlist"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully created playlist",
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
    public ResponseEntity<String> createPlaylist(
            @PathVariable int userId,
            @RequestBody CreatePlaylistRequest playlistRequest) {

        // Fetch the creator of the playlist (the current user)
        Optional<User> creatorOpt = Optional.ofNullable(userRepository.findById(userId));
        if (!creatorOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Creator user not found\"}");
        }
        User creator = creatorOpt.get();

        // Create the playlist object and add the creator as a member
        GroupPlaylist playlist = new GroupPlaylist();
        playlist.addUser(creator); // Add the creator to the playlist

        // Set the playlist name if provided
        if (playlistRequest.getName() != null && !playlistRequest.getName().trim().isEmpty()) {
            playlist.setName(playlistRequest.getName());
        }

        // Add additional users to the playlist if provided
        List<String> userEmails = playlistRequest.getUsers();
        if (userEmails != null && !userEmails.isEmpty()) {
            for (String email : userEmails) {
                Optional<User> userOpt = Optional.ofNullable(userRepository.findByEmailId(email));
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (playlist.hasUser(user)) {
                        continue; // Skip if the user is already in the playlist
                    }
                    playlist.addUser(user);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("{\"error\": \"User not found: " + email + "\"}");
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
    @PutMapping("/users/{userId}/playlists/{playlistId}/updateName")
    @Operation(
            summary = "edit a user's group playlist name",
            description = "update the group playlist name of a user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully changed name",
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
    @PostMapping("/users/{userId}/playlists/{playlistId}/addUser")
    @Operation(
            summary = "add a user to a group playlist",
            description = "adding a user to an existing group playlist"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added user",
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
    @GetMapping("/users/{userId}/playlists/{playlistId}/users")
    @Operation(
            summary = "get all users in a playlist",
            description = "fetch the users in an existing group playlist"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retieved users",
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
    @GetMapping("/users/{userId}/playlists/{playlistId}/songs")
    @Operation(
            summary = "gets songs in a group playlist",
            description = "fetches the song list in an existing group playlist"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved songs",
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
    @GetMapping("/playlists")
    @Operation(
            summary = "get all group playlists and users and songs",
            description = "playlists and the users and songs within them"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved",
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
    public ResponseEntity<List<GroupPlaylist>> getAllPlaylists() {
        List<GroupPlaylist> playlists = playlistRepo.findAll();
        return ResponseEntity.ok(playlists);
    }

    // Get both users and songs in the playlist
    @GetMapping("/users/{userId}/playlists/{playlistId}")
    @Operation(
            summary = "get users and songs in a group playlist",
            description = "fetch users and songs pertaining to an existing group playlist"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved",
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

    // Method to get the currently logged-in user by ID
    private User getCurrentUserById(int userId) {
        return userRepository.findById(userId); // Assuming this method already exists in UserRepository
    }

    // Get a user's playlists by user ID
    @GetMapping("/users/{userId}/playlists/myPlaylists")
    @Operation(
            summary = "get a user's group playlist",
            description = "fetch all group playlists that a user is in"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added user",
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
    public ResponseEntity<List<GroupPlaylist>> getUserPlaylists(@PathVariable int userId) {
        // Retrieve the current authenticated user using the user ID
        User currentUser = getCurrentUserById(userId); // Fetch user by ID from the repository

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Handle the case when user is not found
        }

        // Retrieve playlists where the current user is a member
        List<GroupPlaylist> userPlaylists = playlistRepo.findByUsersContaining(currentUser);

        return ResponseEntity.ok(userPlaylists);
    }




    // Add a song to an existing playlist
    @PostMapping("/users/{userId}/playlists/{playlistId}/songs/{songId}")
    @Operation(
            summary = "add a songs to a group playlist",
            description = "adding a song to an existing group playlist"
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
                    description = "invalid id",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<String> addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Integer songId) {
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
    @DeleteMapping("/users/{userId}/playlists/{playlistId}/songs/{songId}")
    @Operation(
            summary = "delete a song from a group playlist",
            description = "deleting a from an existing group playlist"
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
    public ResponseEntity<String> removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Integer songId) {
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

    //User leaving and removing themselves the current user from a specific playlist
    @DeleteMapping("/users/{userId}/playlists/{playlistId}/remove")
    @Operation(
            summary = "delete a user from a group playlist",
            description = "a user deleting and removing themselves from a group playlist"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully removed user",
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
    public ResponseEntity<String> removeUserFromPlaylist(@PathVariable int userId, @PathVariable Long playlistId) {
        // Fetch the playlist
        Optional<GroupPlaylist> playlistOpt = playlistRepo.findById(playlistId);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Playlist not found\"}");
        }
        GroupPlaylist playlist = playlistOpt.get();

        // Fetch the user
        User user = userRepository.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found\"}");
        }

        // Check if the user is part of the playlist
        if (!playlist.hasUser(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User not in the playlist\"}");
        }

        // Remove the user from the playlist and save
        playlist.getUsers().remove(user);
        playlistRepo.save(playlist);

        return ResponseEntity.ok("{\"message\": \"User removed from playlist\"}");
    }


}
