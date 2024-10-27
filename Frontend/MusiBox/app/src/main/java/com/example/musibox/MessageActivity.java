package com.example.musibox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private ImageButton house;
    private ImageButton addUserButton;
    private ImageButton messageButton;
    private ImageButton userButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        messageList = new ArrayList<>();


        ArrayList<String> friendEmails = getIntent().getStringArrayListExtra("friendEmails");

        // Example: Create dummy messages for the friends
        if (friendEmails != null) {
            for (String email : friendEmails) {
                messageList.add(new Message(email, "Message from " + email)); // Replace with actual messages
            }
        }


        messageAdapter = new MessageAdapter(messageList, this);
        recyclerView.setAdapter(messageAdapter);


        house = findViewById(R.id.home);
        addUserButton = findViewById(R.id.adduser);
        messageButton = findViewById(R.id.message);
        userButton = findViewById(R.id.user);

        // Set up button click listeners for navigation
        house.setOnClickListener(v -> {
            Intent intent = new Intent(MessageActivity.this, MainPage.class);
            startActivity(intent);
        });

        addUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(MessageActivity.this, FriendsActivity.class);
            startActivity(intent);
        });

        messageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MessageActivity.this, Message.class);
            startActivity(intent);
        });

        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(MessageActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
    }
}