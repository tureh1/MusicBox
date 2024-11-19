package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private String friendEmail;
    private String userEmail;
    private WebSocketClient webSocketClient;
    private EditText messageInput;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private Button sendButton;
    private ImageButton backButton;
    private TextView friendName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = sharedPreferences.getString("emailId", null); // Default to null if not found
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if not found

        if (email != null && userId != -1) {
            // Use the email and userId to populate fields or make requests
            Log.d("ChatActivity", "Logged-in email: " + email);
        } else {
            // Handle missing data (e.g., redirect to login)
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Optionally finish this activity
        }


        // Initialize views and variables
        friendEmail = getIntent().getStringExtra("friendEmail");
        userEmail = getIntent().getStringExtra("userEmail");

        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerView);
        friendName = findViewById(R.id.friendname);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        friendName.setText(friendEmail);
        // Fetch old messages and set up WebSocket
        fetchOldMessages();
        setupWebSocket();
        setupButtons();
    }

    private void setupWebSocket() {
        String wsUrl = "ws://10.90.72.167:8080/chat/" + userEmail + "/" + friendEmail;
        URI uri;
        try {
            uri = new URI(wsUrl);
        } catch (Exception e) {
            Log.e("ChatActivity", "Invalid WebSocket URL", e);
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("ChatActivity", "WebSocket opened");
            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(() -> {
                    String currentTimestamp = formatCurrentTimestamp();
                    messageList.add(new ChatMessage(message, false, currentTimestamp));
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("ChatActivity", "WebSocket closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.e("ChatActivity", "WebSocket error: ", ex);
            }
        };

        webSocketClient.connect();
    }

    private void fetchOldMessages() {
        String url = "http://10.90.72.167:8080/messages?email=" + userEmail + "&friendEmail=" + friendEmail;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray messagesArray = new JSONArray(response);

                        for (int i = 0; i < messagesArray.length(); i++) {
                            JSONObject messageObject = messagesArray.getJSONObject(i);
                            String sender = messageObject.getString("sender");
                            String content = messageObject.getString("content");
                            String timestamp = messageObject.getString("timestamp"); // Make sure this is correct

                            // Determine if the message was sent by the user
                            boolean isSentByUser = sender.equals(userEmail);

                            // Create ChatMessage with the correct timestamp from the backend
                            messageList.add(new ChatMessage(content, isSentByUser, timestamp));
                        }

                        // Notify the adapter and scroll to the last message
                        chatAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    } catch (JSONException e) {
                        Log.e("ChatActivity", "JSON Parsing error: ", e);
                    }
                },
                error -> Log.e("ChatActivity", "Volley error: ", error));

        queue.add(stringRequest);
    }

    private void setupButtons() {
        sendButton.setOnClickListener(v -> sendMessage());
        backButton.setOnClickListener(v -> finish());
    }

    private void sendMessage() {
        String message = messageInput.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (webSocketClient != null && webSocketClient.isOpen()) {
            try {
                webSocketClient.send(message);

                String currentTimestamp = formatCurrentTimestamp();
                messageList.add(new ChatMessage(message, true, currentTimestamp));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                messageInput.setText("");
            } catch (Exception e) {
                Log.e("ChatActivity", "Error sending message: ", e);
            }
        } else {
            Toast.makeText(this, "Unable to send message", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
