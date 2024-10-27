    package com.example.musibox;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.os.Bundle;
    import android.os.Message;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.util.Log;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    import com.android.volley.Request;
    import com.android.volley.toolbox.JsonArrayRequest;
    import com.android.volley.toolbox.JsonObjectRequest;
    import org.json.JSONException;
    import org.json.JSONObject;
    import java.util.ArrayList;
    import java.util.List;

    public class FriendsActivity extends AppCompatActivity implements FriendsAdapter.OnFriendDeleteListener, FriendsAdapter.OnFriendUpdateListener, UserAdapter.OnUserClickListener {
        private EditText searchBar;
        private RecyclerView friendsList;
        private List<User> userList;
        private UserAdapter userAdapter;
        private RecyclerView recyclerView;
        private FriendsAdapter friendsAdapter;
        private List<Friend> friendList;
        private ImageButton house;
        private ImageButton message;
        private ImageButton user;
        private ImageButton adduser;

        @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_friends);

            searchBar = findViewById(R.id.search_bar);
            friendsList = findViewById(R.id.friend_list);
            house = findViewById(R.id.home);
            message = findViewById(R.id.message);
            user = findViewById(R.id.user);
            adduser = findViewById(R.id.adduser);
            userList = new ArrayList<>();
            userAdapter = new UserAdapter(userList, this);
            friendsList.setLayoutManager(new LinearLayoutManager(this));
            friendsList.setAdapter(userAdapter);

            // Initialize RecyclerView and Adapter
            recyclerView = findViewById(R.id.friendsRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            friendList = new ArrayList<>();
            friendsAdapter = new FriendsAdapter(friendList, this, this);
            recyclerView.setAdapter(friendsAdapter);

            house.setOnClickListener(v -> {
                Intent intent = new Intent(FriendsActivity.this, MainPage.class);
                startActivity(intent);
            });

            user.setOnClickListener(v -> {
                Intent intent = new Intent(FriendsActivity.this, UserProfileActivity.class);
                startActivity(intent);
            });

            adduser.setOnClickListener(v -> {
                Intent intent = new Intent(FriendsActivity.this, FriendsActivity.class);
                startActivity(intent);
            });
            message.setOnClickListener(v -> {
                Intent intent = new Intent(FriendsActivity.this, MessageActivity.class);
                startActivity(intent);
            });
            // Set up the search bar
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

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
            findViewById(R.id.activity_friends_root).setOnTouchListener((v, event) -> {
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


            // Fetch the friends list from the backend
            fetchFriends();
        }

        private void fetchUsers(String query) {
            String url = "http://10.90.72.167:8080/users";  // Update with your actual URL

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            userList.clear();  // Clear existing list before adding new data

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
                            Toast.makeText(FriendsActivity.this, "Failed to parse users", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(FriendsActivity.this, "Failed to fetch users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
        }

        @Override
        public void onUserClick(String email) {
            sendFriendEmailRequest(email); // Call the POST method with the clicked email
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
                            Toast.makeText(FriendsActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FriendsActivity.this, FriendsActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FriendsActivity.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(FriendsActivity.this, "Request Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }

        private void fetchFriends() {
            int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);

            if (userId == -1) {
                Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.90.72.167:8080/users/" + userId + "/friends";

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            friendList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject friendObject = response.getJSONObject(i);
                                String friendName = friendObject.getString("friendName");
                                String friendEmail = friendObject.getString("friendEmail");
                                friendList.add(new Friend(friendName, friendEmail));
                            }
                            friendsAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FriendsActivity.this, "Failed to parse friends", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("FriendsActivity", "Error: " + error.toString());
                        Toast.makeText(FriendsActivity.this, "Failed to fetch friends: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
        }

        @Override
        public void onFriendDelete(Friend friend) {
            deleteFriend(friend);
        }

        private void deleteFriend(Friend friend) {
            int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);
            if (userId == -1) {
                Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.90.72.167:8080/friends/" + friend.getFriendEmail();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                    response -> {
                        Toast.makeText(FriendsActivity.this, "Friend deleted", Toast.LENGTH_SHORT).show();
                        fetchFriends(); // Refresh the friends list
                    },
                    error -> {
                        Log.e("FriendsActivity", "Error: " + error.toString());
                        Toast.makeText(FriendsActivity.this, "Failed to delete friend: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }

        @Override
        public void onFriendUpdate(String friendEmail, String newFriendName) {
            updateFriend(friendEmail, newFriendName);
        }

        private void updateFriend(String friendEmail, String newFriendName) {
            String url = "http://10.90.72.167:8080/friends/email/" + friendEmail;

            JSONObject updatedFriendJson = new JSONObject();
            try {
                updatedFriendJson.put("friendName", newFriendName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, updatedFriendJson,

                    response -> {
                        Toast.makeText(FriendsActivity.this, "Friend updated", Toast.LENGTH_SHORT).show();
                        fetchFriends(); // Refresh the friends list
                    },
                    error -> {
                        Log.e("FriendsActivity", "Error: " + error.toString());
                        Toast.makeText(FriendsActivity.this, "Failed to update friend: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }