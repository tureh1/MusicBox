/*
package onetomany.Favorites;

import jakarta.persistence.*;
import lombok.Data;
import onetomany.Song.Song;
import onetomany.Users.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "favorites")
@Data
public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "favorites_songs",
            joinColumns = @JoinColumn(name = "favorites_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs = new ArrayList<>();

    public void addSong(Song song) {
        if (!songs.contains(song)) {
            songs.add(song);
        }
    }

    public void removeSong(Song song) {
        songs.remove(song);
    }

    public boolean containsSong(Song song) {
        return songs.contains(song);
    }
}
*/
