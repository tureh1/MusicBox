package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.handshake.ServerHandshake;

public class ChatActivity2 extends AppCompatActivity implements WebSocketListener {

    private Button sendBtn;
    private EditText msgEtx;
    private TextView msgTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        sendBtn = findViewById(R.id.sendBtn2);
        msgEtx = findViewById(R.id.msgEdt2);
        msgTv = findViewById(R.id.tx2);

        WebSocketManager2.getInstance().setWebSocketListener(this);

        sendBtn.setOnClickListener(v -> {
            String message = msgEtx.getText().toString();
            WebSocketManager2.getInstance().sendMessage(message);
            ChatMessageManager.getInstance().addMessage("Chat 2: " + message);
            msgEtx.setText("");
            updateMessageView();
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
        ChatMessageManager.getInstance().addMessage("Chat 2 (Received): " + message);
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