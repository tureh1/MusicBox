package com.example.demo.websocket;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Represents a WebSocket chat server for handling real-time communication
 * between users. Each user connects to the server using their unique
 * username.
 *
 * This class is annotated with Spring's `@ServerEndpoint` and `@Component`
 * annotations, making it a WebSocket endpoint that can handle WebSocket
 * connections at the "/chat/{username}" endpoint.
 *
 * Example URL: ws://localhost:8080/chat/username
 *
 * The server provides functionality for broadcasting messages to all connected
 * users and sending messages to specific users.
 */
@ServerEndpoint("/chat/{username}")
@Component
public class ChatServer {

    // Store all socket session and their corresponding username
    // Two maps for the ease of retrieval by key
    private static Map<Session, String> sessionUsernameMap = new ConcurrentHashMap<>();
    private static Map<String, Session> usernameSessionMap = new ConcurrentHashMap<>();

    // NEW: Track online users to manage user statuses
    private static Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    // Server-side logger
    private final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    /**
     * This method is called when a new WebSocket connection is established.
     *
     * @param session represents the WebSocket session for the connected user.
     * @param username username specified in path parameter.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {

        // Server-side log
        logger.info("[onOpen] " + username);

        // Handle the case of a duplicate username
        if (usernameSessionMap.containsKey(username)) {
            session.getBasicRemote().sendText("Username already exists");
            session.close();
        } else {
            // Map current session with username
            sessionUsernameMap.put(session, username);

            // Map current username with session
            usernameSessionMap.put(username, session);

            // NEW: Mark user as online in onlineUsers set
            onlineUsers.add(username);

            // Send a welcome message to the user joining in
            sendMessageToParticularUser(username, "Welcome to the chat server, " + username);

            // Broadcast to everyone in the chat
            broadcast("User: " + username + " has joined the chat.");
        }
    }

    /**
     * Handles incoming WebSocket messages from a client.
     *
     * @param session The WebSocket session representing the client's connection.
     * @param message The message received from the client.
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {

        // Get the username by session
        String username = sessionUsernameMap.get(session);

        // Server-side log
        logger.info("[onMessage] " + username + ": " + message);

        // NEW: Handle status query command to check if a user is online
        if (message.startsWith("@status")) {
            String[] splitMessage = message.split("\\s+", 2);
            if (splitMessage.length == 2) {
                String targetUsername = splitMessage[1];
                checkUserStatus(username, targetUsername);
            } else {
                sendMessageToParticularUser(username, "Usage: @status <username>");
            }
        } else if (message.startsWith("@")) {
            // Direct message to a user using the format "@username <message>"
            String[] splitMsg = message.split("\\s+", 2);
            String destUserName = splitMsg[0].substring(1);    // @username and get rid of "@"
            String actualMessage = splitMsg.length > 1 ? splitMsg[1] : "";
            sendMessageToParticularUser(destUserName, "[DM from " + username + "]: " + actualMessage);
            sendMessageToParticularUser(username, "[DM to " + destUserName + "]: " + actualMessage);
        } else {
            // Message to whole chat
            broadcast(username + ": " + message);
        }
    }

    /**
     * Handles the closure of a WebSocket connection.
     *
     * @param session The WebSocket session that is being closed.
     */
    @OnClose
    public void onClose(Session session) throws IOException {

        // Get the username from session-username mapping
        String username = sessionUsernameMap.get(session);

        // Server-side log
        logger.info("[onClose] " + username);

        // Remove user from memory mappings
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

        // NEW: Mark user as offline by removing from onlineUsers set
        onlineUsers.remove(username);

        // Send the message to chat
        broadcast(username + " disconnected");
    }

    /**
     * Handles WebSocket errors that occur during the connection.
     *
     * @param session   The WebSocket session where the error occurred.
     * @param throwable The Throwable representing the error condition.
     */
    @OnError
    public void onError(Session session, Throwable throwable) {

        // Get the username from session-username mapping
        String username = sessionUsernameMap.get(session);

        // Do error handling here
        logger.info("[onError]" + username + ": " + throwable.getMessage());
    }

    /**
     * Checks if a user is online and sends the status to the requesting user.
     *
     * @param requesterUsername The username of the user making the request.
     * @param targetUsername The username of the user whose status is being requested.
     */
    private void checkUserStatus(String requesterUsername, String targetUsername) {
        if (onlineUsers.contains(targetUsername)) {
            sendMessageToParticularUser(requesterUsername, "User " + targetUsername + " is online.");
        } else {
            sendMessageToParticularUser(requesterUsername, "User " + targetUsername + " is offline.");
        }
    }

    /**
     * Sends a message to a specific user in the chat (DM).
     *
     * @param username The username of the recipient.
     * @param message  The message to be sent.
     */
    private void sendMessageToParticularUser(String username, String message) {
        try {
            Session session = usernameSessionMap.get(username);
            if (session != null) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            logger.info("[DM Exception] " + e.getMessage());
        }
    }

    /**
     * Broadcasts a message to all users in the chat.
     *
     * @param message The message to be broadcasted to all users.
     */
    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("[Broadcast Exception] " + e.getMessage());
            }
        });
    }
}
