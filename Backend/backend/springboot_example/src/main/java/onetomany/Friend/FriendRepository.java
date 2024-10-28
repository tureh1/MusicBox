package onetomany.Friend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import onetomany.Users.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {
    Friend findByFriendEmail(String friendEmail);

    List<Friend> findByUser(User user);

    // Custom query to find a friend by email and user ID
    @Query("SELECT f FROM Friend f WHERE f.friendEmail = :friendEmail AND f.user.id = :userId")
    Friend findByFriendEmailAndUserId(@Param("friendEmail") String friendEmail, @Param("userId") int userId);

    List<Friend> findByUserId(int userId); // Example of a modified query without isAccepted

    List<Friend> findByUserIdAndIsAccepted(int userId, boolean b);
}
