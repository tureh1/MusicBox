package onetomany.Friend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import onetomany.Users.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Friend findByFriendEmail(String friendEmail);
    List<Friend> findByUser(User user);

    @Query("SELECT f FROM Friend f WHERE f.friendEmail = :friendEmail AND f.user.id = :userId")
    Friend findByFriendEmailAndUserId(@Param("friendEmail") String friendEmail, @Param("userId") int userId);

    List<Friend> findByUserIdAndIsAccepted(int userId, boolean b);

    Friend findByUserIdAndFriendEmail(int id, String emailId);
}
