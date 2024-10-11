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

public class ForgotActivity extends AppCompatActivity {

    private EditText newemail, newpassword, newconfirm;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        newemail = findViewById(R.id.newemail);
        newpassword = findViewById(R.id.newpassword);
        newconfirm = findViewById(R.id.newconfirm);
        Button update = findViewById(R.id.update);

        requestQueue = com.example.musibox.VolleySingleton.getInstance(this).getRequestQueue();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handleUpdate()) {
                    // Send email and password to the backend
                    sendPasswordRequest(newemail.getText().toString().trim(), newpassword.getText().toString().trim());
                }
            }
        });
    }

    private boolean handleUpdate() {
        String usernameInput = newemail.getText().toString().trim();
        String passwordInput = newpassword.getText().toString().trim();
        String confirmInput = newconfirm.getText().toString().trim();

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

    private void sendPasswordRequest(String email, String password) {
        String url = "http://10.90.72.167:8080/newpass/" + email;

        // Create a JSON object with the new password
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("newPassword", password); // Include only the new password
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the JsonObjectRequest for the PUT request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, requestData,
                response -> {
                    // Handle the success response
                    try {
                        String message = response.getString("message");
                        Toast.makeText(ForgotActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Navigate to LoginActivity
                        Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ForgotActivity.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle the error response
                    Toast.makeText(ForgotActivity.this, "Failed to update password: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the request to the Volley queue
        com.example.musibox.VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}