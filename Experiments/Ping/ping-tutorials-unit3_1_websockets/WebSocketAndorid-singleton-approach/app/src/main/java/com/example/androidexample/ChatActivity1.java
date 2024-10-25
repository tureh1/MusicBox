package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.handshake.ServerHandshake;

public class ChatActivity1 extends AppCompatActivity implements WebSocketListener {

    private Button sendBtn, backMainBtn;
    private EditText msgEtx;
    private TextView msgTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);

        sendBtn = findViewById(R.id.sendBtn);
        backMainBtn = findViewById(R.id.backMainBtn);
        msgEtx = findViewById(R.id.msgEdt);
        msgTv = findViewById(R.id.tx1);

        WebSocketManager1.getInstance().setWebSocketListener(this);

        sendBtn.setOnClickListener(v -> {
            String message = msgEtx.getText().toString();
            WebSocketManager1.getInstance().sendMessage(message);
            ChatMessageManager.getInstance().addMessage("Chat 1: " + message);
            msgEtx.setText("");
            updateMessageView();
        });

        backMainBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMessageView();
    }

    private void updateMessageView() {
        StringBuilder messages = new StringBuilder();
        for (String message : ChatMessageManager.getInstance().getMessages()) {
            messages.append(message).append("\n");
        }
        msgTv.setText(messages.toString());
    }

    @Override
    public void onWebSocketMessage(String message) {
        ChatMessageManager.getInstance().addMessage("Chat 1 (Received): " + message);
        updateMessageView();
    }



        /**
         * In Android, all UI-related operations must be performed on the main UI thread
         * to ensure smooth and responsive user interfaces. The 'runOnUiThread' method
         * is used to post a runnable to the UI thread's message queue, allowing UI updates
         * to occur safely from a background or non-UI thread.
         */


    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}
}