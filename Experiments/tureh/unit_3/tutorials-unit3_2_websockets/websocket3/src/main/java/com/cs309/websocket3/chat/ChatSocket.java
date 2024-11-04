package com.cs309.websocket3.chat;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value = "/chat/{email}")  // WebSocket URL with sender's email as a path parameter
public class ChatSocket {

	private static MessageRepository msgRepo;

	@Autowired
	public void setMessageRepository(MessageRepository repo) {
		msgRepo = repo;
	}

	// Maps to store sessions with user emails
	private static Map<Session, String> sessionEmailMap = new Hashtable<>();
	private static Map<String, Session> emailSessionMap = new Hashtable<>();

	private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

	@OnOpen
	public void onOpen(Session session, @PathParam("email") String email) throws IOException {
		logger.info("User connected: " + email);

		// Store user session information
		sessionEmailMap.put(session, email);
		emailSessionMap.put(email, session);

		// Send previous chat history to newly connected user
		sendMessageToUser(email, getChatHistory(email));
	}

	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		logger.info("Received message: " + message);
		String senderEmail = sessionEmailMap.get(session);

		// Validate message format: should start with "@friendEmail messageContent"
		if (message.startsWith("@")) {
			String[] splitMessage = message.split(" ", 2);
			if (splitMessage.length < 2) {
				sendMessageToUser(senderEmail, "Error: Message format should be '@friendEmail messageContent'");
				return;
			}

			String friendEmail = splitMessage[0].substring(1);  // Get friendEmail without '@'
			String messageContent = splitMessage[1];

			// Check if the friend is online
			if (emailSessionMap.containsKey(friendEmail)) {
				// Send the message to the friend
				sendMessageToUser(friendEmail, senderEmail + ": " + messageContent);

				// Notify sender that the message was sent
				sendMessageToUser(senderEmail, "You to " + friendEmail + ": " + messageContent);

				// Save the message in the repository
				msgRepo.save(new Message(senderEmail, friendEmail, messageContent));
			} else {
				// Notify sender that the friend is offline
				sendMessageToUser(senderEmail, "User " + friendEmail + " is offline.");
			}
		} else {
			sendMessageToUser(senderEmail, "Error: Message must start with '@friendEmail'");
		}
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		String email = sessionEmailMap.get(session);
		sessionEmailMap.remove(session);
		emailSessionMap.remove(email);
		logger.info("User disconnected: " + email);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.error("WebSocket error", throwable);
	}

	// Sends a message to a specific user based on their email
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

	// Retrieves chat history between the connected user and all others
	private String getChatHistory(String email) {
		List<Message> messages = msgRepo.findAll();
		StringBuilder history = new StringBuilder();

		for (Message message : messages) {
			// Include messages where this user was either the sender or the recipient
			if (message.getUserName().equals(email) || message.getFriendEmail().equals(email)) {
				history.append(message.getUserName()).append(": ").append(message.getContent()).append("\n");
			}
		}
		return history.toString();
	}
}
