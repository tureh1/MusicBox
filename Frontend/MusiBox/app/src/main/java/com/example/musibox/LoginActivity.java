package com.example.musibox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button loginButton;
    TextView forgotPassword;
    TextView signUpLink;
    //private static final String URL_STRING_REQ = "http://coms-3090-048.class.las.iastate.edu:8080/add";
    //private static final String URL_STRING_REQ ="37c895cd8d87345b/users";
    //private static final String URL_STRING_REQ = "https://coms-3090-048.class.las.iastate.edu/Persons/Users";
    private static final String URL_STRING_REQ ="https://87ec7542-7140-4d4d-9388-3813a059485d.mock.pstmn.io/test/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        signUpLink = findViewById(R.id.signup);

        forgotPassword.setOnClickListener(view -> {
            // Navigate to ForgotPasswordActivity or show a Toast for now
            Toast.makeText(LoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(intent);
        });

        signUpLink.setOnClickListener(view -> {
            // Navigate to SignUpActivity or show a Toast for now
            Toast.makeText(LoginActivity.this, "Sign Up clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });


        loginButton.setOnClickListener(view -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            // Call the login function with Volley
            login(user, pass);
        });
    }

    private void login(String user, String pass) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_STRING_REQ,
                response -> {
                    // Handle successful login response here
                    Toast.makeText(LoginActivity.this, "Login Successful: " + response, Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error response
                    Toast.makeText(LoginActivity.this, "Login Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", user);
                params.put("password", pass);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}