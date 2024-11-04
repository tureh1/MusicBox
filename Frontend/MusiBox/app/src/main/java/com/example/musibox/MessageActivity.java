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
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.OnMessageClickListener {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private ImageButton house;
    private ImageButton addUserButton;
    private ImageButton messageButton;
    private ImageButton userButton;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initViews();
        setupRecyclerView();
        setupNavigationButtons();
        fetchFriendsEmails();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        house = findViewById(R.id.home);
        addUserButton = findViewById(R.id.adduser);
        messageButton = findViewById(R.id.message);
        userButton = findViewById(R.id.user);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
    }

    private void setupNavigationButtons() {
        house.setOnClickListener(v -> startActivity(new Intent(MessageActivity.this, MainPage.class)));
        addUserButton.setOnClickListener(v -> startActivity(new Intent(MessageActivity.this, FriendsActivity.class)));
        messageButton.setOnClickListener(v -> Toast.makeText(this, "You are already in Message Activity", Toast.LENGTH_SHORT).show());
        userButton.setOnClickListener(v -> startActivity(new Intent(MessageActivity.this, UserProfileActivity.class)));
    }

    private void fetchFriendsEmails() {
        int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.90.72.167:8080/users/" + userId + "/friends";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                this::handleFriendsResponse,
                this::handleErrorResponse);

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void handleFriendsResponse(JSONArray response) {
        try {
            messageList.clear();
            if (response.length() == 0) {
                Toast.makeText(this, "No friends found", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < response.length(); i++) {
                JSONObject friendObject = response.getJSONObject(i);
                String friendEmail = friendObject.getString("friendEmail");
                String messageContent = "Message from " + friendEmail; // Placeholder for actual messages
                messageList.add(new Message(friendEmail, messageContent));
            }
            messageAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("MessageActivity", "JSON parsing error: " + e.getMessage());
            Toast.makeText(this, "Failed to parse friends", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleErrorResponse(Throwable error) {
        Log.e("MessageActivity", "Error: " + error.toString());
        Toast.makeText(this, "Failed to fetch friends: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void deleteFriend(String friendEmail) {
        int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.90.72.167:8080/users/" + userId + "/friends/" + friendEmail;

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(this, "Friend deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchFriendsEmails();
                },
                error -> {
                    Log.e("MessageActivity", "Error deleting friend: " + error.toString());
                    Toast.makeText(this, "Failed to delete friend: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(deleteRequest);
    }

    @Override
    public void onMessageClick(String friendEmail) {
        int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);
        if (userId != -1) {
            fetchUserEmail(userId, email -> {
                if (friendEmail != null && !friendEmail.isEmpty()) {
                    Intent intent = new Intent(MessageActivity.this, ChatActivity.class);
                    intent.putExtra("friendEmail", friendEmail);
                    intent.putExtra("userEmail", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Friend email is missing", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFriendDelete(Message friend) {
        deleteFriend(friend.getUsername());
    }


    private void fetchUserEmail(int userId, UserEmailCallback callback) {
        String url = "http://10.90.72.167:8080/users/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        userEmail = jsonObject.getString("emailId");
                        callback.onEmailFetched(userEmail);
                    } catch (JSONException e) {
                        Log.e("MessageActivity", "Error parsing user email: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("MessageActivity", "Error fetching user email: " + error.toString());
                    Toast.makeText(this, "Failed to fetch user email: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public interface UserEmailCallback {
        void onEmailFetched(String email);
    }
}