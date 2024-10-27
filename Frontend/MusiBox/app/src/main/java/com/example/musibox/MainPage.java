package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class MainPage extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private EditText searchBar;
    private RecyclerView friendsList;
    private List<User> userList;
    private UserAdapter userAdapter;
    private ImageButton house;
    private ImageButton message;
    private ImageButton user;
    private ImageButton adduser;

    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);


        String userEmail = getSharedPreferences("user_data", MODE_PRIVATE)
                .getString("email", null); // Provide a default value (null) if not found


        if (userEmail != null) {
            Toast.makeText(this, "Logged in as: " + userEmail, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
        }

        searchBar = findViewById(R.id.search_bar);
        friendsList = findViewById(R.id.friend_list);
        house = findViewById(R.id.home);
        message = findViewById(R.id.message);
        user = findViewById(R.id.user);
        adduser = findViewById(R.id.adduser);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this); // Pass the listener
        friendsList.setLayoutManager(new LinearLayoutManager(this));
        friendsList.setAdapter(userAdapter);

        adduser.setOnClickListener(v -> {
            // Action to perform when the message button is clicked
            Intent intent = new Intent(MainPage.this, FriendsActivity.class);
            startActivity(intent);
        });
        user.setOnClickListener(v -> {
            // Action to perform when the message button is clicked
            Intent intent = new Intent(MainPage.this, UserProfileActivity.class);
            startActivity(intent);
        });
        message.setOnClickListener(v -> {
            // Action to perform when the message button is clicked
            Intent intent = new Intent(MainPage.this, MessageActivity.class);
            startActivity(intent);
        });
        house.setOnClickListener(v -> {
            // Action to perform when the message button is clicked
            Intent intent = new Intent(MainPage.this, MainPage.class);
            startActivity(intent);
        });
        // Add TextChangeListener to the search bar for dynamic search
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    fetchUsers(s.toString());
                } else {
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


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
                                // Add new User to the list if query matches
                                userList.add(new User(email));
                            }
                        }

                        // Notify adapter of data change
                        userAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainPage.this, "Failed to parse users", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(MainPage.this, "Failed to fetch users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }
    @Override
    public void onUserClick(String email) {
        sendFriendEmailRequest(email); // Call the POST method with the clicked email
    }

    private void sendFriendEmailRequest(String email) {

        int userId = getSharedPreferences("user_data", MODE_PRIVATE)
                .getInt("userId", -1); // Provide a default value if not found

        if (userId == -1) {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.90.72.167:8080/users/" + userId + "/addFriend";

        // Create a JSON object with the friend email
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("friendEmail", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the JsonObjectRequest for the POST request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                response -> {
                    // Handle the success response here
                    try {
                        String message = response.getString("message");

                        // Handle success response
                        Toast.makeText(MainPage.this, message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainPage.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle the error response
                    Toast.makeText(MainPage.this, "Request Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });


        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}