package com.example.musibox;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button loginButton;
    TextView forgotPassword;
    TextView signUpLink;
    private boolean isFirstClick = true; // Flag to check if it's the first click
    private static final String URL_STRING_REQ ="37c895cd8d87345b/users";


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

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ForgotPasswordActivity or show a Toast for now
                Toast.makeText(LoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();

                 Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                 startActivity(intent);
            }
        });


        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to SignUpActivity or show a Toast for now
                Toast.makeText(LoginActivity.this, "Sign Up clicked", Toast.LENGTH_SHORT).show();

                 Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                 startActivity(intent);
            }
        });

        // Common focus change listener for both username and password fields
        View.OnFocusChangeListener clearTextListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus && isFirstClick) {
                    username.setText("");  // Clear the default text from username
                    password.setText("");  // Clear the default text from password
                    isFirstClick = false;  // Ensure this only happens once for both fields
                }
            }
        };
        // Attach the common listener to both fields
        username.setOnFocusChangeListener(clearTextListener);
        password.setOnFocusChangeListener(clearTextListener);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("user") && password.getText().toString().equals("password")) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "The User and Password do not match.  .", Toast.LENGTH_SHORT).show();
                }

                //Clears after log in attempt
                username.setText("");
                password.setText("");
            }
        });
    }

}
