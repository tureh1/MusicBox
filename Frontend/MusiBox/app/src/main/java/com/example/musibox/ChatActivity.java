// ChatActivity.java
package com.example.musibox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.handshake.ServerHandshake;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private Button sendBtn, backMainBtn;
    private EditText msgEdt;
    private TextView msgTv;
    private String friendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sendBtn = findViewById(R.id.sendBtn);
        backMainBtn = findViewById(R.id.backMainBtn);
        msgEdt = findViewById(R.id.msgEdt);
        msgTv = findViewById(R.id.msgTv);

        // Get friend's email from intent
        Intent intent = getIntent();
        friendEmail = intent.getStringExtra("friendEmail");

        if (friendEmail == null) {
            Toast.makeText(this, "No friend selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up WebSocket connection for the friend
        String websocketUrl = "ws://10.90.72.167:8080/chat/" + friendEmail; // Replace with the correct server URL
        WebSocketManager.getInstance().setWebSocketListener(this);
        WebSocketManager.getInstance().connectWebSocket(websocketUrl);

        sendBtn.setOnClickListener(v -> {
            String message = msgEdt.getText().toString();
            WebSocketManager.getInstance().sendMessage(message);
            msgEdt.setText("");
            appendMessage("You: " + message);
        });

        backMainBtn.setOnClickListener(view -> {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketManager.getInstance().removeWebSocketListener();
        WebSocketManager.getInstance().disconnectWebSocket();
    }

    private void appendMessage(String message) {
        String currentText = msgTv.getText().toString();
        msgTv.setText(currentText + "\n" + message);
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        runOnUiThread(() -> Toast.makeText(this, "Connected to " + friendEmail, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> appendMessage(friendEmail + ": " + message));
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "Server" : "Client";
        runOnUiThread(() -> appendMessage("--- Connection closed by " + closedBy + "\nReason: " + reason));
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.d("WebSocket", "Error: " + ex.getMessage());
        runOnUiThread(() -> Toast.makeText(this, "WebSocket error: " + ex.getMessage(), Toast.LENGTH_SHORT).show());
    }
}