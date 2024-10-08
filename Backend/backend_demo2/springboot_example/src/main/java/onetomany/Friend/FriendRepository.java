package onetomany.Friend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Friend findById(int id);

    // Find friends by user ID and accepted status
    List<Friend> findByUserIdAndIsAccepted(int userId, Boolean isAccepted);

    // Optionally: find friend requests sent to a user
    List<Friend> findByFriendIdAndIsAccepted(int friendId, Boolean isAccepted);
}
