package onetomany.GroupPlaylist;

import jakarta.persistence.*;
import lombok.Data;
import onetomany.Song.Song;
import onetomany.Users.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "group_playlists")
@Data
public class GroupPlaylist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;  // This field can remain as is, it will be set later

    // Many-to-Many relationship for users
    @ManyToMany
    @JoinTable(
            name = "playlist_users",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    // Many-to-Many relationship for songs
    @ManyToMany
    @JoinTable(
            name = "playlist_songs",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private Set<Song> songs = new HashSet<>();

    // Constructor without name
    public GroupPlaylist() {}

    // Optional constructor with name
    public GroupPlaylist(String name) {
        this.name = name;
    }

    // Add user to the playlist if not already present
    public boolean addUser(User user) {
        return users.add(user); // returns true if the user was added, false if already present
    }

    // Check if a specific user is in the playlist
    public boolean hasUser(User user) {
        return users.contains(user);
    }

    // Add song to the playlist if not already present
    public boolean addSong(Song song) {
        return songs.add(song); // returns true if the song was added, false if already present
    }

    // Check if a song is already in the playlist
    public boolean hasSong(Song song) {
        return songs.contains(song);
    }
}
