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
@ServerEndpoint(value = "/rating/{albumId}")
public class RatingSocket {

    private static onetomany.Rating.RatingRepository ratingRepo;

    @Autowired
    public void setRatingRepository(onetomany.Rating.RatingRepository repo) {
        ratingRepo = repo;
    }

    // Store all sessions for live updates
    private static Map<Session, String> sessionAlbumMap = new Hashtable<>();
    private static Map<String, Session> albumSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(RatingSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("albumId") String albumId) throws IOException {
        logger.info("Entered into Open for album: " + albumId);

        // Store connecting album information
        sessionAlbumMap.put(session, albumId);
        albumSessionMap.put(albumId, session);

        // Send current rating to the newly connected user
        sendCurrentRatingToUser(albumId, session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        logger.info("Received rating update: " + message);

        String albumId = sessionAlbumMap.get(session);
        String username = "anonymous";  // replace with real username if available

        // Parse and update rating
        int rating = Integer.parseInt(message);
        ratingRepo.save(new onetomany.Rating.Rating(albumId, username, rating));

        // Broadcast the new average rating
        double avgRating = ratingRepo.getAverageRating(albumId);
        broadcastRatingUpdate(albumId, avgRating);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String albumId = sessionAlbumMap.get(session);
        sessionAlbumMap.remove(session);
        albumSessionMap.remove(albumId);
        logger.info("Closed session for album: " + albumId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("Error occurred: " + throwable.getMessage());
    }

    private void sendCurrentRatingToUser(String albumId, Session session) throws IOException {
        double avgRating = ratingRepo.getAverageRating(albumId);
        session.getBasicRemote().sendText("Current rating for album " + albumId + ": " + avgRating);
    }

    private void broadcastRatingUpdate(String albumId, double avgRating) {
        sessionAlbumMap.forEach((session, album) -> {
            if (album.equals(albumId)) {
                try {
                    session.getBasicRemote().sendText("Updated rating for album " + albumId + ": " + avgRating);
                } catch (IOException e) {
                    logger.info("Exception: " + e.getMessage());
                }
            }
        });
    }
}
