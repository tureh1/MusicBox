package com.example.musibox;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlaylistActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView usernames;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        backButton = findViewById(R.id.back);
        usernames = findViewById(R.id.usernames);
        String groupName = getIntent().getStringExtra("groupName");
        if (groupName != null)
            usernames.setText(groupName);
        setupButtons();
        }


    private void setupButtons() {
        backButton.setOnClickListener(v -> finish());
    }
}