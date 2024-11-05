package com.example.musibox;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.OnMessageClickListener, UserAdapter.OnUserClickListener {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private ImageButton house;
    private ImageButton addUserButton;
    private ImageButton messageButton;
    private ImageButton userButton;
    private String userEmail;

    private EditText searchBar;
    private RecyclerView friendsList;
    private List<User> userList;
    private UserAdapter userAdapter;


    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initViews();
        setupRecyclerView();
        setupNavigationButtons();
        fetchFriendsEmails();
        // Set up the search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    fetchUsers(s.toString());
                } else {
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                    friendsList.setVisibility(View.GONE); // Hide if no input
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        findViewById(R.id.message_activity_root).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (friendsList.getVisibility() == View.VISIBLE || friendsList.getVisibility() == View.GONE) {
                    friendsList.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    searchBar.clearFocus(); // Clear focus from search bar
                }
            }
            return true;
        });


        searchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                friendsList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                if (userList.isEmpty()) {
                    friendsList.setVisibility(View.GONE);
                }

                recyclerView.setVisibility(View.VISIBLE);
            }
        });




    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.search_bar);
        friendsList = findViewById(R.id.friend_list);
        house = findViewById(R.id.home);
        addUserButton = findViewById(R.id.adduser);
        messageButton = findViewById(R.id.message);
        userButton = findViewById(R.id.user);

    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, this);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        friendsList.setLayoutManager(new LinearLayoutManager(this));
        friendsList.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
    }

    private void setupNavigationButtons() {
        house.setOnClickListener(v -> startActivity(new Intent(MessageActivity.this, MainPage.class)));
        addUserButton.setOnClickListener(v -> startActivity(new Intent(MessageActivity.this, CreateGroupActivity.class)));
        messageButton.setOnClickListener(v -> startActivity(new Intent(MessageActivity.this, MessageActivity.class)));
        userButton.setOnClickListener(v -> startActivity(new Intent(MessageActivity.this, UserProfileActivity.class)));
    }

    private void fetchUsers(String query) {
        String url = "http://10.90.72.167:8080/users";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        userList.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject userObject = response.getJSONObject(i);
                            String email = userObject.getString("emailId");

                            // Filter users by checking if the email starts with the search query
                            if (email.toLowerCase().startsWith(query.toLowerCase())) {
                                userList.add(new User(email));
                            }
                        }

                        // Notify adapter of data change
                        userAdapter.notifyDataSetChanged();

                        // Show user list if there are results
                        if (!userList.isEmpty()) {
                            friendsList.setVisibility(View.VISIBLE);
                        } else {
                            friendsList.setVisibility(View.GONE);
                        }

                        // Hide main friends RecyclerView
                        recyclerView.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MessageActivity.this, "Failed to parse users", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(MessageActivity.this, "Failed to fetch users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    public void onUserClick(String email) {
        sendFriendEmailRequest(email);
    }

    private void sendFriendEmailRequest(String email) {
        int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1); // Provide a default value if not found

        if (userId == -1) {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.90.72.167:8080/users/" + userId + "/addFriend";
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("friendEmail", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(MessageActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MessageActivity.this, MessageActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MessageActivity.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(MessageActivity.this, "Request Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
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

    void deleteFriend(String friendEmail) {
        int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.90.72.167:8080/users/" + userId + "/friends/" + friendEmail;

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Intent intent = new Intent(MessageActivity.this, MessageActivity.class);
                    Toast.makeText(this, "Friend deleted successfully", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
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

    public interface UserEmailCallback {
        void onEmailFetched(String email);
    }
}
