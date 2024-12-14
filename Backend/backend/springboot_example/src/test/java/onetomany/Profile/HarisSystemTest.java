package onetomany.Profile;


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
public class HarisSystemTest {
    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        // Reset test data by clearing the database if needed
         RestAssured.given()
               .header("Content-Type", "application/json")
             .delete("/profiles/reset"); // Ensure the `/users/reset` endpoint exists for database cleanup
    }

    @Test
    public void testCreateProfile() throws JSONException {
        // Step 1: Create a user
        JSONObject userRequestBody = new JSONObject();
        userRequestBody.put("emailId", "createprofile@example.com");
        userRequestBody.put("password", "securepassword");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(userRequestBody.toString())
                .post("/signup");

        // Step 2: Create a profile for the user
        JSONObject profileRequestBody = new JSONObject();
        profileRequestBody.put("bio", "Profile created successfully");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(profileRequestBody.toString())
                .post("/users/1/profile"); // Replace "1" with the actual userId if dynamically generated

        // Assertions
        assertEquals(200, response.getStatusCode());
        assertEquals("{\"message\":\"Profile created successfully\"}", response.getBody().asString());
    }

    @Test
    public void testGetUserBio() throws JSONException {
        // Step 1: Create a user
        JSONObject userRequestBody = new JSONObject();
        userRequestBody.put("emailId", "userbio@example.com");
        userRequestBody.put("password", "securepassword");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(userRequestBody.toString())
                .post("/signup");

        // Step 2: Create a profile with a bio for the user
        JSONObject bioRequestBody = new JSONObject();
        bioRequestBody.put("bio", "This is a test bio");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(bioRequestBody.toString())
                .post("/users/1/bio");

        // Step 3: Fetch the bio
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .get("/users/1/bio");

        // Assertions
        assertEquals(200, response.getStatusCode());
        assertEquals("This is a test bio", response.getBody().asString());
    }

    @Test
    public void testUpdateProfileBio() throws JSONException {
        // Step 1: Create a user
        JSONObject userRequestBody = new JSONObject();
        userRequestBody.put("emailId", "updatebio@example.com");
        userRequestBody.put("password", "securepassword");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(userRequestBody.toString())
                .post("/signup");

        // Step 2: Create a profile for the user
        JSONObject createProfileRequestBody = new JSONObject();
        createProfileRequestBody.put("bio", "Initial bio");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(createProfileRequestBody.toString())
                .post("/users/1/profile");

        // Step 3: Update the profile bio
        JSONObject updateProfileRequestBody = new JSONObject();
        updateProfileRequestBody.put("bio", "Updated bio");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(updateProfileRequestBody.toString())
                .put("/users/1/profile");

        // Assertions
        assertEquals(200, response.getStatusCode());
        assertEquals("{\"message\":\"Profile updated successfully\"}", response.getBody().asString());
    }

    @Test
    public void testDeleteProfile() throws JSONException {
        // Step 1: Create a user
        JSONObject userRequestBody = new JSONObject();
        userRequestBody.put("emailId", "deleteprofile@example.com");
        userRequestBody.put("password", "securepassword");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(userRequestBody.toString())
                .post("/signup");

        // Step 2: Create a profile for the user
        JSONObject profileRequestBody = new JSONObject();
        profileRequestBody.put("bio", "Bio to be deleted");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(profileRequestBody.toString())
                .post("/users/1/profile");

        // Step 3: Delete the profile
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .delete("/users/1/profile");

        // Assertions
        assertEquals(200, response.getStatusCode());
        assertEquals("{\"message\":\"Profile deleted successfully\"}", response.getBody().asString());
    }


}
