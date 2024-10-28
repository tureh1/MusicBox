package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.java_websocket.handshake.ServerHandshake;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private EditText messageInput;
    private List<ChatMessage> messageList;
    private String friendEmail; // Store the email of the friend we are chatting with

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);
        ImageButton back = findViewById(R.id.back);

        // Get the friend's email from the intent
        friendEmail = getIntent().getStringExtra("friendEmail");

        // Set up RecyclerView with ChatAdapter
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        back.setOnClickListener(v -> finish()); // Go back to the previous activity

        // Initialize WebSocket and set listener
        WebSocketManager.getInstance().setWebSocketListener(this);
        WebSocketManager.getInstance().connectWebSocket("ws://yourserverurl/chat/" + friendEmail); // Connect to the WebSocket for this specific chat

        // Set up send button click listener
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                WebSocketManager.getInstance().sendMessage(message); // Send message to the WebSocket
                addMessage(new ChatMessage(message, true)); // Add sent message to the list
                messageInput.setText("");
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMessage(ChatMessage message) {
        messageList.add(message);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    // WebSocketListener implementation
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        // Connection established
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> addMessage(new ChatMessage(message, false))); // Add received message
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        // Connection closed
    }

    @Override
    public void onWebSocketError(Exception ex) {
        ex.printStackTrace();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketManager.getInstance().disconnectWebSocket(); // Disconnect WebSocket on activity destroy
    }
}