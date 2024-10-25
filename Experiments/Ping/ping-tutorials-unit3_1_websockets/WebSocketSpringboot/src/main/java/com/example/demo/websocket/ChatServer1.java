package com.example.demo.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@ServerEndpoint("/chat/1/{username}")
@Component
public class ChatServer1 {

    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(ChatServer1.class);
    private final MessageManager messageManager = MessageManager.getInstance();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        logger.info("[onOpen] " + username);

        if (usernameSessionMap.containsKey(username)) {
            session.getBasicRemote().sendText("Username already exists");
            session.close();
        } else {
            sessionUsernameMap.put(session, username);
            usernameSessionMap.put(username, session);
            sendMessageToUser(username, "Welcome to the chat server, " + username);

            broadcast("User: " + username + " has joined the chat");
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String username = sessionUsernameMap.get(session);
        logger.info("[onMessage] " + username + ": " + message);

        messageManager.addMessage(username + ": " + message);
        broadcast(username + ": " + message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String username = sessionUsernameMap.get(session);
        logger.info("[onClose] " + username);

        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

        broadcast(username + " disconnected");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String username = sessionUsernameMap.get(session);
        logger.info("[onError]" + username + ": " + throwable.getMessage());
    }

    private void sendMessageToUser(String username, String message) {
        try {
            usernameSessionMap.get(username).getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.info("[DM Exception] " + e.getMessage());
        }
    }

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