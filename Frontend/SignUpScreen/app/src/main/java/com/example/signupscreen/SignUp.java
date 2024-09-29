package com.example.signupscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    // Declare your UI components
    private EditText username, password, confirm;
    private Button signUpButton;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup); // Ensure this matches your XML file name

        // Initialize UI components
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm);
        signUpButton = findViewById(R.id.SignUpButton);
        forgotPassword = findViewById(R.id.forgotPassword);

        // Set up click listener for the sign-up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
                Intent intent = new Intent(SignUp.this, LoginActivity.class);

                startActivity(intent);
            }
        });
    }

    private void handleSignUp() {
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String confirmInput = confirm.getText().toString().trim();

        // Basic validation
        if (usernameInput.isEmpty() || passwordInput.isEmpty() || confirmInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordInput.equals(confirmInput)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulate sign-up logic (this is where you'd handle backend communication)
        Toast.makeText(this, "Sign Up Successful for " + usernameInput, Toast.LENGTH_SHORT).show();
        // You can also start a new activity here if needed
    }
}