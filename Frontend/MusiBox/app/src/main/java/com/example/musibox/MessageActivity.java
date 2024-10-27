package com.example.musibox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

        // Fetch the friends' emails from the backend
        fetchFriendsEmails();

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
            Intent intent = new Intent(MessageActivity.this, MessageActivity.class);
            startActivity(intent);
        });

        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(MessageActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
    }

    private void fetchFriendsEmails() {
        int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.90.72.167:8080/users/" + userId + "/friends";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        messageList.clear(); // Clear existing messages
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject friendObject = response.getJSONObject(i);
                            String friendEmail = friendObject.getString("friendEmail");
                            String messageContent = "Message from " + friendEmail; // Placeholder for actual messages
                            messageList.add(new Message(friendEmail, messageContent));
                        }
                        messageAdapter.notifyDataSetChanged(); // Notify the adapter of data change
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MessageActivity.this, "Failed to parse friends", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("MessageActivity", "Error: " + error.toString());
                    Toast.makeText(MessageActivity.this, "Failed to fetch friends: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }
}