package com.example.loginscreen;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button loginButton;
    private boolean isFirstClick = true; // Flag to check if it's the first click 


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

       username = findViewById(R.id.username);
       password = findViewById(R.id.password);
       loginButton = findViewById(R.id.loginButton);

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
                   Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
               } else {
                   Toast.makeText(MainActivity.this, "The User and Password do not match. Try again or Forget Password/Sign Up .", Toast.LENGTH_SHORT).show();
               }
                //Clears after log in attempt
               username.setText("");
               password.setText("");
           }
       });
    }
}