/*package onetomany.Posts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, String> {
    Optional<Post> findById(int id);
    List<Post> findByUserId(String userId);
    List<Post> findByAlbumId(int albumId);
    void deleteById(int id);
}
*/