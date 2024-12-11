package onetomany.Users;

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
public class TuSystemTest {

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        // Reset test data by clearing the database if needed
        RestAssured.given()
                .header("Content-Type", "application/json")
                .delete("/users/reset"); // Ensure the `/users/reset` endpoint exists for database cleanup
    }

    @Test
    public void testUserSignup() throws JSONException {
        String uniqueEmail = "testuser_" + System.currentTimeMillis() + "@example.com";

        JSONObject requestBody = new JSONObject();
        requestBody.put("emailId", uniqueEmail);
        requestBody.put("password", "securepassword");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post("/signup");

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"message\":\"signup successfully\"}", response.getBody().asString());
    }


    @Test
    public void testUserLogin() throws JSONException {
        // Create a user first
        JSONObject requestBody = new JSONObject();
        requestBody.put("emailId", "testuser@example.com");
        requestBody.put("password", "securepassword");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post("/signup");

        // Attempt to login
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post("/login");

        assertEquals(200, response.getStatusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("\"message\":\"Login successful\""));
    }

    @Test
    public void testFetchAllUsers() throws JSONException {
        // Ensure user signup
        JSONObject requestBody = new JSONObject();
        requestBody.put("emailId", "testuser@example.com");
        requestBody.put("password", "securepassword");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post("/signup");

        // Fetch all users
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .get("/users");

        // Debug: Print response body
        System.out.println("Response Body: " + response.getBody().asString());

        // Assertions
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("testuser@example.com"));
    }

    @Test
    public void testDeleteUser() throws JSONException {
        // Create a user first
        JSONObject requestBody = new JSONObject();
        requestBody.put("emailId", "testuser@example.com");
        requestBody.put("password", "securepassword");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post("/signup");

        // Delete the user
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .delete("/users/testuser@example.com");

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"message\":\"success\"}", response.getBody().asString());
    }
}
