package onetomany.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserNameAndFriendEmail(String userName, String friendEmail);
    List<Message> findByFriendEmailAndUserName(String friendEmail, String userName);

    @Query("SELECT m FROM Message m WHERE (m.userName = :email AND m.friendEmail = :friendEmail) " +
            "OR (m.userName = :friendEmail AND m.friendEmail = :email) " +
            "ORDER BY m.sent ASC")
    List<Message> findMessagesBetweenUsers(@Param("email") String email, @Param("friendEmail") String friendEmail);

    // New method to check for duplicate messages
    List<Message> findByUserNameAndFriendEmailAndContent(String userName, String friendEmail, String content);
}
