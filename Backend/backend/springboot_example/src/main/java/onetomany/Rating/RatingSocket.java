package onetomany.Rating;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import onetomany.Song.Song;
import onetomany.Song.SongRepository;
import onetomany.Song.SongSocket;
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
    private final Logger logger = LoggerFactory.getLogger(SongSocket.class);

    // ObjectMapper for parsing JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session, @PathParam("email") String email, @PathParam("songId") int songId) {
        logger.info("User connected: " + email);
        sessionEmailMap.put(session, email);

        // Send song details when the WebSocket connection is established
        sendSongDetails(session, songId);
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("songId") int songId, String message) throws IOException {
        logger.info("Message received: " + message);

        // Parse the incoming JSON message
        JsonNode jsonMessage = objectMapper.readTree(message);
        int rating;

        // Check if the rating field exists and is an integer
        if (jsonMessage.has("rating")) {
            rating = jsonMessage.get("rating").asInt();
        } else {
            sendMessageToUser(session, createJsonResponse("error", "Rating field is missing."));
            return;
        }

        // Validate the rating
        if (rating < 1 || rating > 5) {
            sendMessageToUser(session, createJsonResponse("error", "Rating must be between 1 and 5."));
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

            sendMessageToUser(session, createJsonResponse("success", "Your rating has been updated successfully."));
        } else {
            // If no rating exists, add a new one
            Rating newRating = new Rating(userEmail, songId, rating);
            ratingRepository.save(newRating);

            updateAverageRating(songId);
            broadcastRatingUpdate(songId);

            sendMessageToUser(session, createJsonResponse("success", "Your rating has been submitted."));
        }
    }

    // Send song details to the user
    private void sendSongDetails(Session session, int songId) {
        Song song = songRepository.findById(songId).orElse(null);
        if (song != null) {
            String songDetailsMessage = String.format(
                    "{\"title\": \"%s\", \"artist\": \"%s\", \"rating\": %.1f}",
                    song.getTitle(), song.getArtist(), song.getAverageRating());
            sendMessageToUser(session, songDetailsMessage);
        } else {
            sendMessageToUser(session, createJsonResponse("error", "Song not found."));
        }
    }

    // Calculate and update the average rating of the song
    private void updateAverageRating(int songId) {
        List<Rating> ratings = ratingRepository.findBySongId(songId);
        double average = ratings.stream().mapToInt(Rating::getRating).average().orElse(0);

        Song song = songRepository.findById(songId).orElse(null);
        if (song != null) {
            song.setAverageRating(average);
            songRepository.save(song);
        }
    }

    // Broadcast updated rating to all WebSocket connections
    private void broadcastRatingUpdate(int songId) {
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
    private String getAverageRatingMessage(int songId) {
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

    // Helper function to create a JSON response message
    private String createJsonResponse(String status, String message) {
        return String.format("{\"status\": \"%s\", \"message\": \"%s\"}", status, message);
    }
}
