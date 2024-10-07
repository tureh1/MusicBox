package com.example.musibox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
<<<<<<< HEAD:Frontend/MusiBox/app/src/main/java/com/example/musibox/SignupActivity.java

=======
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
>>>>>>> 781aa96 (update):Frontend/MusiBox/app/src/main/java/com/example/musibox/SignUp.java


import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {

    private EditText email, password, confirm;
<<<<<<< HEAD:Frontend/MusiBox/app/src/main/java/com/example/musibox/SignupActivity.java

=======
>>>>>>> 781aa96 (update):Frontend/MusiBox/app/src/main/java/com/example/musibox/SignUp.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm);
        Button signUpButton = findViewById(R.id.SignUpButton);

<<<<<<< HEAD:Frontend/MusiBox/app/src/main/java/com/example/musibox/SignupActivity.java
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
=======
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
>>>>>>> 781aa96 (update):Frontend/MusiBox/app/src/main/java/com/example/musibox/SignUp.java
    }

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

<<<<<<< HEAD:Frontend/MusiBox/app/src/main/java/com/example/musibox/SignupActivity.java

=======
    private void sendSignUpRequest(String email, String password) {
        String url = "http://10.90.72.167:8080/users";

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
                    Toast.makeText(SignUp.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();

                    // Navigate to LoginActivity
                    Intent intent = new Intent(SignUp.this, LoginActivity.class);
                    startActivity(intent);
                },
                error -> {
                    // Handle the error here
                    Toast.makeText(SignUp.this, "Sign Up Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the request to the Volley queue
        com.example.musibox.VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
>>>>>>> 781aa96 (update):Frontend/MusiBox/app/src/main/java/com/example/musibox/SignUp.java
