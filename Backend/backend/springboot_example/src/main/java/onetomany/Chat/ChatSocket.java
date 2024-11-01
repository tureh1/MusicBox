package onetomany.Chat;

import java.io.IOException;
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
@ServerEndpoint(value = "/chat/{email}")
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
    public void onOpen(Session session, @PathParam("email") String email) throws IOException {
        logger.info("User connected: " + email);

        sessionEmailMap.put(session, email);
        emailSessionMap.put(email, session);

        sendMessageToUser(email, getChatHistory(email));
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String senderEmail = sessionEmailMap.get(session);

        if (message.startsWith("@")) {
            String[] splitMessage = message.split(" ", 2);
            if (splitMessage.length < 2) {
                sendMessageToUser(senderEmail, "Error: Message format should be '@friendEmail messageContent'");
                return;
            }

            String friendEmail = splitMessage[0].substring(1);
            String messageContent = splitMessage[1];

            // Check if both users are friends with isAccepted=true
            if (isFriendshipAccepted(senderEmail, friendEmail)) {
                if (emailSessionMap.containsKey(friendEmail)) {
                    sendMessageToUser(friendEmail, senderEmail + ": " + messageContent);
                    sendMessageToUser(senderEmail, "You to " + friendEmail + ": " + messageContent);

                    msgRepo.save(new Message(senderEmail, friendEmail, messageContent));
                } else {
                    sendMessageToUser(senderEmail, "User " + friendEmail + " is offline.");
                }
            } else {
                sendMessageToUser(senderEmail, "Cannot send message. " + friendEmail + " is not your friend.");
            }
        } else {
            sendMessageToUser(senderEmail, "Error: Message must start with '@friendEmail'");
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

    private String getChatHistory(String email) {
        List<Message> messages = msgRepo.findAll();
        StringBuilder history = new StringBuilder();

        for (Message message : messages) {
            if (message.getUserName().equals(email) || message.getFriendEmail().equals(email)) {
                history.append(message.getUserName()).append(": ").append(message.getContent()).append("\n");
            }
        }
        return history.toString();
    }
}
