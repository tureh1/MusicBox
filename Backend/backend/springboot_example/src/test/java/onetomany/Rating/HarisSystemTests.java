package onetomany.Rating;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class HarisSystemTests {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        // Reset test data by clearing the database if needed
        RestAssured.given()
                .header("Content-Type", "application/json")
                .delete("/rating/reset"); // Ensure the `/users/reset` endpoint exists for database cleanup
    }

    @Test
    public void testRateSong_AddNewRating() {
        // Create a new rating for a song
        JSONObject ratingJson = new JSONObject();
        try {
            ratingJson.put("songId", 1);
            ratingJson.put("userEmail", "testuser@example.com");
            ratingJson.put("rating", 4);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(ratingJson.toString())
                .post("/ratings/rate");

        assertEquals(200, response.getStatusCode());  // Check if the response is successful
        assertTrue(response.body().asString().contains("Rating added for song:"), "Response body should contain success message");
    }

    @Test
    public void testRateSong_UpdateExistingRating() throws JSONException {
        // Update an existing rating for a song
        JSONObject ratingJson = new JSONObject();
        try {
            ratingJson.put("songId", 1);
            ratingJson.put("userEmail", "testuser@example.com");
            ratingJson.put("rating", 5);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // First, add the rating
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(ratingJson.toString())
                .post("/ratings/rate");

        // Now update the rating
        ratingJson.put("rating", 3);  // Update to a different rating

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(ratingJson.toString())
                .post("/ratings/rate");

        assertEquals(200, response.getStatusCode());  // Check if the response is successful
        assertTrue(response.body().asString().contains("Rating updated for song:"), "Response body should contain success message");
    }

    @Test
    public void testGetAverageRating_Success() {
        // Get the average rating for a song
        Response response = RestAssured.given()
                .get("/ratings/1/average");

        assertEquals(200, response.getStatusCode());  // Check if the response is successful
        String responseBody = response.body().asString();
        assertTrue(responseBody.contains("Average rating for"), "Response should contain the average rating for the song");
    }

    @Test
    public void testRateSong_SongNotFound() {
        // Attempt to rate a song that doesn't exist
        JSONObject ratingJson = new JSONObject();
        try {
            ratingJson.put("songId", 999);  // Assuming songId 999 doesn't exist
            ratingJson.put("userEmail", "testuser@example.com");
            ratingJson.put("rating", 4);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(ratingJson.toString())
                .post("/ratings/rate");

        assertEquals(404, response.getStatusCode());  // Expecting not found status code
        assertTrue(response.body().asString().contains("Song not found"), "Response body should contain 'Song not found'");
    }
}
