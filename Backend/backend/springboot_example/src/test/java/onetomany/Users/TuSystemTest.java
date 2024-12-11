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

/*
author @tureh
coverage test completed
 */

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
        // Step 1: Sign up a new user
        JSONObject signupRequest = new JSONObject();
        signupRequest.put("emailId", "admin@example.com");
        signupRequest.put("password", "adminpassword");

        Response signupResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(signupRequest.toString())
                .post("/signup");

        // Debug: Check if signup was successful
        System.out.println("Signup Response Status: " + signupResponse.getStatusCode());
        System.out.println("Signup Response Body: " + signupResponse.getBody().asString());
        assertEquals(200, signupResponse.getStatusCode(), "Signup failed.");

        // Step 2: Login as admin
        JSONObject loginRequest = new JSONObject();
        loginRequest.put("emailId", "admin@example.com");
        loginRequest.put("password", "adminpassword");

        Response loginResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(loginRequest.toString())
                .post("/login");

        // Debug: Print response status and body
        System.out.println("Login Response Status: " + loginResponse.getStatusCode());
        System.out.println("Login Response Body: " + loginResponse.getBody().asString());

        // Assertions
        assertEquals(200, loginResponse.getStatusCode(), "Login endpoint failed.");
        assertTrue(
                loginResponse.getBody().asString().contains("\"message\":\"Login successful\""),
                "Login response does not contain the expected success message. Response: " + loginResponse.getBody().asString()
        );
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
    public void testGetUserById() throws JSONException {
        // Sign up a new user
        JSONObject requestBody = new JSONObject();
        requestBody.put("emailId", "testuser@example.com");
        requestBody.put("password", "securepassword");

        Response signupResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post("/signup");

        // Debug: Check if signup was successful
        System.out.println("Signup Response: " + signupResponse.getBody().asString());
        assertEquals(200, signupResponse.getStatusCode(), "Signup failed, cannot proceed with test.");

        // Manually fetch all users to find the ID (if signup response lacks it)
        Response allUsersResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .get("/users");

        System.out.println("All Users Response: " + allUsersResponse.getBody().asString());
        assertEquals(200, allUsersResponse.getStatusCode());

        // Extract userId for the created user
        int userId = allUsersResponse.jsonPath().getInt("find { it.emailId == 'testuser@example.com' }.id");

        // Fetch user by ID
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .get("/users/" + userId);

        // Debug: Print response body for analysis
        System.out.println("Get User by ID Response: " + response.getBody().asString());

        // Assertions
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("testuser@example.com"));
    }


    @Test
    public void testUpdatePassword() throws JSONException {
        // Sign up a new user
        JSONObject requestBody = new JSONObject();
        requestBody.put("emailId", "testuser@example.com");
        requestBody.put("password", "securepassword");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post("/signup");

        // Change the password
        JSONObject passwordRequest = new JSONObject();
        passwordRequest.put("newPassword", "newsecurepassword");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(passwordRequest.toString())
                .put("/newpass/testuser@example.com");

        // Assertions
        assertEquals(200, response.getStatusCode());
        assertEquals("{\"message\":\"password reset successfully\"}", response.getBody().asString());
    }

    @Test
    public void testToggleUserStatus() throws JSONException {
        // Sign up a user
        JSONObject requestBody = new JSONObject();
        requestBody.put("emailId", "testuser@example.com");
        requestBody.put("password", "securepassword");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .post("/signup");

        // Toggle user status
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .post("/users/testuser@example.com/status");

        // Assertions
        assertEquals(200, response.getStatusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("User successfully banned") || responseBody.contains("User successfully unbanned"));
    }

    @Test
    public void testGetAllUsers() {
        // Fetch all users
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .get("/users");

        // Assertions
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("emailId"));
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
