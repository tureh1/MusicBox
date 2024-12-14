package onetomany.GroupPlaylist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class HarisSystemTests {

    @LocalServerPort
    private int port;  // Automatically injects the random port used by Spring Boot

    @Before
    public void setUp() {
        RestAssured.port = port;  // Set the port for RestAssured
        RestAssured.baseURI = "http://localhost";

        // Reset test data by clearing the database if needed
        RestAssured.given()
                .header("Content-Type", "application/json")
                .delete("/playlists/reset"); // Ensure the `/users/reset` endpoint exists for database cleanup
    }

    @Test
    public void testCreateGroupPlaylist() throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("name", "Rock Playlist");
        requestBody.put("description", "A playlist of classic rock songs");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post("/playlists");

        assertEquals(201, response.getStatusCode());
        JSONObject responseBody = new JSONObject(response.getBody().asString());
        assertEquals("Rock Playlist", responseBody.getString("name"));
        assertEquals("A playlist of classic rock songs", responseBody.getString("description"));
    }


    @Test
    public void testGetAllGroupPlaylists() throws JSONException {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .get("/playlists");

        assertEquals(200, response.getStatusCode());
        JSONArray responseBody = new JSONArray(response.getBody().asString());
        assertTrue(responseBody.length() > 0, "Expected at least one group playlist");
    }

    @Test
    public void testUpdateGroupPlaylist() throws JSONException {
        // First, create a playlist to update
        JSONObject createRequest = new JSONObject();
        createRequest.put("name", "Jazz Playlist");
        createRequest.put("description", "A playlist of smooth jazz");
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(createRequest.toString())
                .post("/playlists");

        assertEquals(201, createResponse.getStatusCode());
        JSONObject createdPlaylist = new JSONObject(createResponse.getBody().asString());
        int playlistId = createdPlaylist.getInt("id");

        // Update the created playlist
        JSONObject updateRequest = new JSONObject();
        updateRequest.put("name", "Updated Jazz Playlist");
        updateRequest.put("description", "A playlist of classic jazz");

        Response updateResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(updateRequest.toString())
                .put("/playlists/" + playlistId);

        assertEquals(200, updateResponse.getStatusCode());
        JSONObject updatedPlaylist = new JSONObject(updateResponse.getBody().asString());
        assertEquals("Updated Jazz Playlist", updatedPlaylist.getString("name"));
        assertEquals("A playlist of classic jazz", updatedPlaylist.getString("description"));
    }


    @Test
    public void testDeleteGroupPlaylist() throws JSONException {
        // First, create a playlist to delete
        JSONObject createRequest = new JSONObject();
        createRequest.put("name", "Pop Playlist");
        createRequest.put("description", "A playlist of pop songs");
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(createRequest.toString())
                .post("/playlists");

        assertEquals(201, createResponse.getStatusCode());
        JSONObject createdPlaylist = new JSONObject(createResponse.getBody().asString());
        int playlistId = createdPlaylist.getInt("id");

        // Delete the created playlist
        Response deleteResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .delete("/playlists/" + playlistId);

        assertEquals(200, deleteResponse.getStatusCode());
        assertTrue(deleteResponse.getBody().asString().contains("Playlist deleted"));

        // Verify that the playlist has been deleted
        Response getResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .get("/playlists/" + playlistId);

        assertEquals(404, getResponse.getStatusCode(), "Expected 404 for deleted playlist");
    }


    @Test
    public void testAddUserToGroupPlaylist() throws JSONException {
        // First, create a playlist to add a user
        JSONObject createRequest = new JSONObject();
        createRequest.put("name", "Electronic Playlist");
        createRequest.put("description", "A playlist of electronic music");
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(createRequest.toString())
                .post("/playlists");

        assertEquals(201, createResponse.getStatusCode());
        JSONObject createdPlaylist = new JSONObject(createResponse.getBody().asString());
        int playlistId = createdPlaylist.getInt("id");

        // Create a user to add to the playlist
        JSONObject userRequest = new JSONObject();
        userRequest.put("username", "john_doe");
        userRequest.put("email", "john_doe@example.com");

        Response userResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(userRequest.toString())
                .post("/users");

        assertEquals(201, userResponse.getStatusCode());
        JSONObject createdUser = new JSONObject(userResponse.getBody().asString());
        int userId = createdUser.getInt("id");

        // Add the user to the playlist
        Response addUserResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .post("/playlists/" + playlistId + "/users/" + userId);

        assertEquals(200, addUserResponse.getStatusCode());
        JSONObject responseBody = new JSONObject(addUserResponse.getBody().asString());
        assertTrue(responseBody.getJSONArray("users").toString().contains("john_doe"));
    }


}
