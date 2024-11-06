package onetomany.GroupPlaylist;

import onetomany.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupPlaylistRepository extends JpaRepository<GroupPlaylist, Long> {
    List<GroupPlaylist> findByUsersContaining(User user);
}
