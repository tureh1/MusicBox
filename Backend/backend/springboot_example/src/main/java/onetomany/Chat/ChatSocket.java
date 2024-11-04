package onetomany.Chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import onetomany.Friend.Friend;
import onetomany.Friend.FriendRepository;
import onetomany.Users.User;
import onetomany.Users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value = "/chat/{email}/{friendEmail}")
public class ChatSocket {

    private static MessageRepository msgRepo;
    private static FriendRepository friendRepo;
    private static UserRepository userRepo;

    @Autowired
    public void setMessageRepository(MessageRepository repo) {
        msgRepo = repo;
    }

    @Autowired
    public void setFriendRepository(FriendRepository repo) {
        friendRepo = repo;
    }

    @Autowired
    public void setUserRepository(UserRepository repo) {
        userRepo = repo;
    }

    private static Map<Session, String> sessionEmailMap = new Hashtable<>();
    private static Map<String, Session> emailSessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("email") String email, @PathParam("friendEmail") String friendEmail) throws IOException {
        logger.info("User connected: " + email + " to chat with " + friendEmail);

        sessionEmailMap.put(session, email);
        emailSessionMap.put(email, session);

        // Check if friendship is accepted before allowing chat
        if (isFriendshipAccepted(email, friendEmail)) {
            List<String> chatHistory = getChatHistory(email, friendEmail);
            for (String msg : chatHistory) {
                sendMessageToUser(email, msg); // Send each message separately
            }
        } else {
            sendMessageToUser(email, "Cannot chat with " + friendEmail + ". Friendship is not confirmed.");
            session.close();
        }
    }

    @OnMessage
    public void onMessage(Session session, String messageContent) throws IOException {
        String senderEmail = sessionEmailMap.get(session);
        String friendEmail = getFriendEmailBySession(session);

        if (friendEmail != null && isFriendshipAccepted(senderEmail, friendEmail)) {
            Message message = new Message(senderEmail, friendEmail, messageContent);
            msgRepo.save(message);

            String timestampedMessage = senderEmail + " [" + message.getFormattedTimestamp() + "]: " + messageContent;

            if (emailSessionMap.containsKey(friendEmail)) {
                sendMessageToUser(friendEmail, timestampedMessage);
            } else {
                sendMessageToUser(senderEmail, "User " + friendEmail + " is offline.");
            }
        } else {
            sendMessageToUser(senderEmail, "Cannot send message. " + friendEmail + " is not your friend or friendship is not confirmed.");
        }
    }


    private boolean isFriendshipAccepted(String user1, String user2) {
        Friend friendRelation1 = friendRepo.findByFriendEmailAndUserId(user2, getUserIdByEmail(user1));
        Friend friendRelation2 = friendRepo.findByFriendEmailAndUserId(user1, getUserIdByEmail(user2));

        return friendRelation1 != null && friendRelation1.isAccepted() && friendRelation2 != null && friendRelation2.isAccepted();
    }

    private int getUserIdByEmail(String email) {
        User user = userRepo.findByEmailId(email);
        return user != null ? user.getId() : -1;
    }

    private String getFriendEmailBySession(Session session) {
        return session.getPathParameters().get("friendEmail");
    }

    @OnClose
    public void onClose(Session session) {
        String email = sessionEmailMap.get(session);
        sessionEmailMap.remove(session);
        emailSessionMap.remove(email);
        logger.info("User disconnected: " + email);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket error", throwable);
    }

    private void sendMessageToUser(String email, String message) {
        try {
            Session recipientSession = emailSessionMap.get(email);
            if (recipientSession != null) {
                recipientSession.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            logger.error("Error sending message: " + e.getMessage(), e);
        }
    }

    private List<String> getChatHistory(String email, String friendEmail) {
        // Retrieve messages in chronological order between the two users
        List<Message> messages = msgRepo.findMessagesBetweenUsers(email, friendEmail);
        List<String> history = new ArrayList<>();

        // Format messages to include sender's name, timestamp, and content
        for (Message message : messages) {
            String formattedMessage = message.getContent();
            history.add(formattedMessage);
        }
        return history;
    }

}
