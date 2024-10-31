package com.example.musibox;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get friendEmail and userEmail from Intent
        friendEmail = getIntent().getStringExtra("friendEmail");
        userEmail = getIntent().getStringExtra("userEmail");
        Log.d("ChatActivity", "User email passed to ChatActivity: " + userEmail); // Log the user email

        // Initialize views
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerView);

        // Set up message list and adapter
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, userEmail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Set up WebSocket connection
        setupWebSocket();

        // Setup button listeners
        setupButtons();
    }

    // Set up WebSocket connection to the backend
    private void setupWebSocket() {
        String wsUrl = "ws://10.90.72.167:8080/chat/" + userEmail + "/" + friendEmail;
        URI uri;
        try {
            uri = new URI(wsUrl);
        } catch (Exception e) {
            Log.e("ChatActivity", "Invalid WebSocket URL", e);
            return;
        }

        // Create WebSocket client
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("ChatActivity", "WebSocket opened");
            }

            @Override
            public void onMessage(String message) {
                // Handle incoming message from server
                runOnUiThread(() -> {
                    messageList.add(new ChatMessage(message, false)); // Message from the friend
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

        // Connect the WebSocket client
        webSocketClient.connect();
    }

    // Set up button listeners
    private void setupButtons() {
        sendButton.setOnClickListener(v -> sendMessage());
        backButton.setOnClickListener(v -> finish());
    }

    // Method to send messages via WebSocket
    private void sendMessage() {
        String message = messageInput.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if WebSocket is connected
        if (webSocketClient != null && webSocketClient.isOpen()) {
            try {
                // Send the message via WebSocket
                webSocketClient.send(message);

                // Add message to the list and show it in the chat view as "sent"
                messageList.add(new ChatMessage(message, true)); // Message from the user
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);

                // Clear the input field
                messageInput.setText("");
            } catch (Exception e) {
                Log.e("ChatActivity", "Error sending message: ", e);
            }
        } else {
            Toast.makeText(this, "WebSocket is not connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close(); // Close the WebSocket connection when activity is destroyed
        }
    }
}
