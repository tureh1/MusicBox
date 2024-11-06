package onetomany.Rating;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import onetomany.Song.Song;
import onetomany.Song.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@ServerEndpoint("/rate/{email}/{songId}")
public class RatingSocket {

    private static RatingRepository ratingRepository;
    private static SongRepository songRepository;

    @Autowired
    public void setRatingRepository(RatingRepository repo) {
        ratingRepository = repo;
    }

    @Autowired
    public void setSongRepository(SongRepository repo) {
        songRepository = repo;
    }

    private static Map<Session, String> sessionEmailMap = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(RatingSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("email") String email) {
        logger.info("User connected: " + email);
        sessionEmailMap.put(session, email);
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("songId") Long songId, String message) throws IOException {
        logger.info("Message received: " + message);
        int rating;
        try {
            rating = Integer.parseInt(message.trim());
        } catch (NumberFormatException e) {
            sendMessageToUser(session, "Invalid rating format. Please send a number between 1 and 5.");
            return;
        }

        if (rating < 1 || rating > 5) {
            sendMessageToUser(session, "Rating must be between 1 and 5.");
            return;
        }

        String userEmail = sessionEmailMap.get(session);

        // Check if the user has already rated this song
        Optional<Rating> existingRating = ratingRepository.findByUserEmailAndSongId(userEmail, songId);

        if (existingRating.isPresent()) {
            // If a rating exists, update it
            Rating ratingToUpdate = existingRating.get();
            ratingToUpdate.setRating(rating);
            ratingRepository.save(ratingToUpdate);

            updateAverageRating(songId);
            broadcastRatingUpdate(songId);

            sendMessageToUser(session, "Your rating has been updated successfully.");
        } else {
            // If no rating exists, add a new one
            Rating newRating = new Rating(userEmail, songId, rating);
            ratingRepository.save(newRating);

            updateAverageRating(songId);
            broadcastRatingUpdate(songId);

            sendMessageToUser(session, "Your rating has been submitted.");
        }
    }

    // Calculate and update the average rating of the song
    private void updateAverageRating(Long songId) {
        List<Rating> ratings = ratingRepository.findBySongId(songId);
        double average = ratings.stream().mapToInt(Rating::getRating).average().orElse(0);

        Song song = songRepository.findById(songId).orElse(null);
        if (song != null) {
            song.setAverageRating(average);
            songRepository.save(song);
        }
    }

    // Broadcast updated rating to all WebSocket connections
    private void broadcastRatingUpdate(Long songId) {
        List<Session> sessions = sessionEmailMap.keySet().stream().collect(Collectors.toList());
        sessions.forEach(session -> {
            try {
                String averageRatingMessage = getAverageRatingMessage(songId);
                session.getBasicRemote().sendText(averageRatingMessage);
            } catch (IOException e) {
                logger.error("Error broadcasting rating update: ", e);
            }
        });
    }


    // Generate the rating message
    private String getAverageRatingMessage(Long songId) {
        Song song = songRepository.findById(songId).orElse(null);
        if (song != null) {
            return "Updated average rating for " + song.getTitle() + ": " + song.getAverageRating();
        }
        return "Song not found.";
    }

    @OnClose
    public void onClose(Session session) {
        String email = sessionEmailMap.remove(session);
        logger.info("User disconnected: " + email);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket error", throwable);
    }

    // Helper function to send a message to the user
    private void sendMessageToUser(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.error("Error sending message: " + e.getMessage());
        }
    }
}