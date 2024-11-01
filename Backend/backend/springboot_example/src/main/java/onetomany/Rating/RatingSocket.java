package onetomany.Rating;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value = "/album/{albumId}/rating")
public class RatingSocket {

    private static RatingRepository ratingRepo;

    @Autowired
    public void setRatingRepository(RatingRepository repo) {
        ratingRepo = repo;
    }

    private static Map<Session, Integer> sessionAlbumMap = new Hashtable<>();
    private static Map<Integer, Session> albumSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(RatingSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("albumId") int albumId) throws IOException {
        logger.info("Entered into Open for album: " + albumId);
        sessionAlbumMap.put(session, albumId);
        albumSessionMap.put(albumId, session);
        sendCurrentRatingToUser(albumId, session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        logger.info("Received rating update: " + message);

        int albumId = sessionAlbumMap.get(session);
        String username = "anonymous";

        int rating = Integer.parseInt(message);
        ratingRepo.save(new Rating(albumId, username, rating));

        double avgRating = ratingRepo.getAverageRating(albumId);  // Call on instance

        broadcastRatingUpdate(albumId, avgRating);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        int albumId = sessionAlbumMap.get(session);
        sessionAlbumMap.remove(session);
        albumSessionMap.remove(albumId);
        logger.info("Closed session for album: " + albumId);
    }

    private void sendCurrentRatingToUser(int albumId, Session session) throws IOException {
        double avgRating = ratingRepo.getAverageRating(albumId);  // Call on instance
        session.getBasicRemote().sendText("Current rating for album " + albumId + ": " + avgRating);
    }

    private void broadcastRatingUpdate(int albumId, double avgRating) {
        sessionAlbumMap.forEach((session, album) -> {
            if (album == albumId) {
                try {
                    session.getBasicRemote().sendText("Updated rating for album " + albumId + ": " + avgRating);
                } catch (IOException e) {
                    logger.info("Exception: " + e.getMessage());
                }
            }
        });
    }
}

