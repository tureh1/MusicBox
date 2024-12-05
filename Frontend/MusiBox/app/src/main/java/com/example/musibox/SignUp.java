package com.example.musibox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The SignUp activity allows the user to create a new account by entering an email, password,
 * and confirming the password. The activity validates the input and sends the data to a backend
 * server for registration.
 */
public class SignUp extends AppCompatActivity {

    private EditText email, password, confirm;


    /**
     * Called when the activity is first created. Initializes the UI components and sets up
     * the sign-up button click listener.
     *
     * @param savedInstanceState The saved instance state (if the activity is being recreated).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm);
        Button signUpButton = findViewById(R.id.SignUpButton);

        // Initialize the Volley request queue
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handleSignUp()) {
                    // Send email and password to the backend
                    sendSignUpRequest(email.getText().toString().trim(), password.getText().toString().trim());
                }
            }
        });

    }

    /**
     * Validates the user input for the sign-up process. Checks that all fields are filled in,
     * the password is at least 6 characters, and the passwords match.
     *
     * @return True if the input is valid, otherwise false.
     */
    private boolean handleSignUp() {
        String usernameInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String confirmInput = confirm.getText().toString().trim();

        if (usernameInput.isEmpty() || passwordInput.isEmpty() || confirmInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwordInput.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!passwordInput.equals(confirmInput)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    /**
     * Sends the sign-up request to the backend server using a POST method. It sends the user's
     * email and password as a JSON object.
     *
     * @param email    The user's email.
     * @param password The user's password.
     */
    private void sendSignUpRequest(String email, String password) {
        String url = "http://10.90.72.167:8080/signup";

        // Create a JSON object with the email and password
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("emailId", email);
            requestData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the JsonObjectRequest for the POST request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                response -> {
                    // Handle the success response here
                    try {
                        String message = response.getString("message");

                        // Check the message to determine if the signup was successful
                        if (message.equals("signup successfully")) {
                            Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SignUp.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle the error here
                    Toast.makeText(SignUp.this, "Sign Up Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
