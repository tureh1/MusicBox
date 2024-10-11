package com.example.musibox;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class homeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Retrieve the email from Intent
        Intent intent = getIntent();
        String email = intent.getStringExtra("emailId");

        // Store email in SharedPreferences for later use
        if (email != null) {
            getSharedPreferences("user_data", MODE_PRIVATE)
                    .edit()
                    .putString("email", email)
                    .apply();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);


        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    // Handle home action
                    return true;
                } else if (item.getItemId() == R.id.navigation_profile) {
                    // Navigate to profile
                    Intent profileIntent = new Intent(homeActivity.this, UserProfileActivity.class);
                    profileIntent.putExtra("emailId",email);
                    startActivity(profileIntent);
                    return true;
                } else if (item.getItemId() == R.id.navigation_messages) {
                    // Handle message action
                    return true;
                }
                return false;
            }
        });
    }
}
