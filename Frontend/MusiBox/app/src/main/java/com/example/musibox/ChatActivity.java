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
import java.text.ParseException;
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
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Fetch old messages from the backend
        fetchOldMessages();

        // Set up WebSocket connection
        setupWebSocket();

        // Setup button listeners
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
                    // Assuming the incoming message is in JSON format
                    String currentTimestamp = formatCurrentTimestamp(); // Get formatted current timestamp
                    messageList.add(new ChatMessage(message, false, currentTimestamp)); // Message from friend
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
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray messagesArray = new JSONArray(response);

                            for (int i = 0; i < messagesArray.length(); i++) {
                                JSONObject messageObj = messagesArray.getJSONObject(i);
                                String content = messageObj.getString("content");
                                boolean isSentByUser = messageObj.getString("type").equals("user-message");
                                String formattedTimestamp = messageObj.getString("timestamp"); // Get the formatted timestamp directly from the response

                                messageList.add(new ChatMessage(content, isSentByUser, formattedTimestamp));
                            }

                            chatAdapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(messageList.size() - 1);

                        } catch (JSONException e) {
                            Log.e("ChatActivity", "JSON Parsing error: ", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ChatActivity", "Volley error: ", error);
                    }
                });

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

                // Get the current timestamp
                String currentTimestamp = formatCurrentTimestamp(); // Get formatted current timestamp
                messageList.add(new ChatMessage(message, true, currentTimestamp));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                messageInput.setText("");
            } catch (Exception e) {
                Log.e("ChatActivity", "Error sending message: ", e);
            }
        } else {
            Toast.makeText(this, "WebSocket is not connected", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatCurrentTimestamp() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }

    private String formatTimestamp(String timestamp) {
        // Assuming timestamp is in ISO 8601 format; you may need to adjust the format according to your backend
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("ChatActivity", "Timestamp parsing error: ", e);
            return timestamp; // Return the original timestamp if parsing fails
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