package onetomany.Song;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import onetomany.Rating.Rating;
import onetomany.Rating.RatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@ServerEndpoint("/rate/{email}")
public class SongSocket {

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

    // When the user connects, send the details of all songs
    @OnOpen
    public void onOpen(Session session, @PathParam("email") String email) {
        logger.info("User connected: " + email);
        sessionEmailMap.put(session, email);

        // Send details of all songs when the WebSocket connection is established
        sendAllSongDetails(session);
    }

    // When a message is received from the client (rating a song)
    @OnMessage
    public void onMessage(Session session, @PathParam("email") String email, String message) throws IOException {
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

        // Extract songId from the message
        int songId = jsonMessage.get("songId").asInt();

        // Check if the user has already rated this song
        Optional<Rating> existingRating = ratingRepository.findByUserEmailAndSongId(userEmail, songId);

        if (existingRating.isPresent()) {
            // If a rating exists, delete the old rating and add a new one
            ratingRepository.delete(existingRating.get()); // Delete the old rating
            Rating newRating = new Rating(userEmail, songId, rating);
            ratingRepository.save(newRating);

            // Recalculate the average rating and broadcast the updated rating
            updateAverageRating(songId);
            broadcastUpdatedSongList();

            sendMessageToUser(session, createJsonResponse("success", "Your rating has been updated successfully."));
        } else {
            // If no rating exists, add a new one
            Rating newRating = new Rating(userEmail, songId, rating);
            ratingRepository.save(newRating);

            // Recalculate the average rating and broadcast the updated rating
            updateAverageRating(songId);
            broadcastUpdatedSongList();

            sendMessageToUser(session, createJsonResponse("success", "Your rating has been submitted."));
        }
    }

    // Send details of all songs to the user, including average ratings
    private void sendAllSongDetails(Session session) {
        List<Song> songs = songRepository.findAll();
        if (songs.isEmpty()) {
            sendMessageToUser(session, createJsonResponse("error", "No songs available."));
        } else {
            for (Song song : songs) {
                String songDetailsMessage = String.format(
                        "{\"id\": %d, \"title\": \"%s\", \"artist\": \"%s\", \"rating\": %.1f, \"cover\": \"%s\"}",
                        song.getId(), song.getTitle(), song.getArtist(), song.getAverageRating(), song.getCover());
                sendMessageToUser(session, songDetailsMessage);
            }
        }
    }

    // Calculate and update the average rating for a song
    private void updateAverageRating(int songId) {
        List<Rating> ratings = ratingRepository.findBySongId(songId);
        double average = ratings.stream().mapToInt(Rating::getRating).average().orElse(0);

        Song song = songRepository.findById(songId).orElse(null);
        if (song != null) {
            song.setAverageRating(average);  // Update the song's average rating
            songRepository.save(song);
        }
    }

    // Broadcast the updated list of songs to all connected users
    private void broadcastUpdatedSongList() {
        List<Song> songs = songRepository.findAll();
        for (Session session : sessionEmailMap.keySet()) {
            try {
                for (Song song : songs) {
                    String songDetailsMessage = String.format(
                            "{\"id\": %d, \"title\": \"%s\", \"artist\": \"%s\", \"rating\": %.1f, \"cover\": \"%s\"}",
                            song.getId(), song.getTitle(), song.getArtist(), song.getAverageRating(), song.getCover());
                    session.getBasicRemote().sendText(songDetailsMessage);
                }
            } catch (IOException e) {
                logger.error("Error broadcasting song list update: ", e);
            }
        }
    }


    private void broadcastNewSong(Song song) {
        for (Session session : sessionEmailMap.keySet()) {
            try {
                String songDetailsMessage = String.format(
                        "{\"id\": %d, \"title\": \"%s\", \"artist\": \"%s\", \"rating\": %.1f, \"cover\": \"%s\"}",
                        song.getId(), song.getTitle(), song.getArtist(), song.getAverageRating(), song.getCover());
                session.getBasicRemote().sendText(songDetailsMessage);
            } catch (IOException e) {
                logger.error("Error broadcasting new song: ", e);
            }
        }
    }


    // Close the WebSocket session when a user disconnects
    @OnClose
    public void onClose(Session session) {
        String email = sessionEmailMap.remove(session);
        logger.info("User disconnected: " + email);
    }

    // Handle WebSocket errors
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
