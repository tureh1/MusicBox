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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.musibox.LoginActivity;


import org.json.JSONException;
import org.json.JSONObject;

public class ForgotActivity extends AppCompatActivity {

 //   private EditText newemail, newpassword, newconfirm;
   // private RequestQueue requestQueue;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_forgotpassword);
//
//        newemail = findViewById(R.id.newemail;
//        newpassword = findViewById(R.id.newpassword);
//        newconfirm = findViewById(R.id.newconfirm);
//        Button update = findViewById(R.id.update);

        // Initialize the Volley request queue
//        requestQueue = com.example.musibox.VolleySingleton.getInstance(this).getRequestQueue();
//
//        update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (handleSignUp()) {
//                    // Send email and password to the backend
//                    sendSignUpRequest(newemail.getText().toString().trim(), newpassword.getText().toString().trim());
//                }
//            }
//        });
//    }
//
//    private boolean handleSignUp() {
//        String usernameInput = newemail.getText().toString().trim();
//        String passwordInput = newpassword.getText().toString().trim();
//        String confirmInput = newconfirm.getText().toString().trim();
//
//        if (usernameInput.isEmpty() || passwordInput.isEmpty() || confirmInput.isEmpty()) {
//            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (passwordInput.length() < 6) {
//            Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!passwordInput.equals(confirmInput)) {
//            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        return true;
//    }
//
//    private void sendSignUpRequest(String email, String password) {
//        String url = "http://coms-3090-048.class.las.iastate.edu:8080/add";
//
//        // Create a JSON object with the email and password
//        JSONObject requestData = new JSONObject();
//        try {
//            requestData.put("emailId", email);
//            requestData.put("password", password);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        // Create the JsonObjectRequest for the POST request
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // Handle the success response here
//                        Toast.makeText(SignUp.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
//
//                        // Navigate to LoginActivity
//                        Intent intent = new Intent(SignUp.this, LoginActivity.class);
//                        startActivity(intent);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // Handle the error here
//                        Toast.makeText(SignUp.this, "Sign Up Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//        // Add the request to the Volley queue
//        com.example.musibox.VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
   }
