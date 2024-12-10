package com.example.musibox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements AdminAdapter.OnUserClickListener {

    private RecyclerView recyclerView;
    private AdminAdapter adminAdapter;
    private List<User> userList;
    private EditText searchBar;
    private String loggedInUserEmail; // Store logged-in user's email
    private ImageButton user,songs,settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.search_bar);
        user = findViewById(R.id.user);
        songs = findViewById(R.id.music);
        settings = findViewById(R.id.settings);

        user.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminActivity.class);
            startActivity(intent);
        });
        songs.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminSongActivity.class);
            startActivity(intent);
        });
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String email = sharedPreferences.getString("emailId", null); // Default to null if not found
        int userId = sharedPreferences.getInt("userId", -1); // Default to -1 if not found
        if (email != null && userId != -1) {
            Log.d("AdminActivity", "Logged-in email: " + email);
        } else {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize user list and adapter
        userList = new ArrayList<>();
        adminAdapter = new AdminAdapter(userList, this); // 'this' refers to the activity implementing OnUserClickListener
        recyclerView.setAdapter(adminAdapter);

        // Load users
        fetchUsers("");

        // Implement search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    // This method is triggered when the "Delete" option is clicked
    @Override
    public void onUserDelete(User user) {
        // Check if the logged-in user is an admin or a regular user
        if (!user.getEmailId().equals(loggedInUserEmail)) {
            sendDeleteRequest(user.getEmailId());
        } else {
            Toast.makeText(this, "Cannot delete yourself", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserBan(User user) {
        // Check if the logged-in user is an admin or a regular user
        if (!user.getEmailId().equals(loggedInUserEmail)) {
            sendBanRequest(user.getEmailId(), true); // Ban the user
        } else {
            Toast.makeText(this, "Cannot ban yourself", Toast.LENGTH_SHORT).show();
        }
    }

    // This method is triggered when the "Ban/Activate" option is clicked
    @Override
    public void onUserBanActivate(User user) {
        // Check if the logged-in user is an admin or a regular user
        if (!user.getEmailId().equals(loggedInUserEmail)) {
            sendBanRequest(user.getEmailId(), !user.isActive()); // Toggle the active status
        } else {
            Toast.makeText(this, "Cannot ban/activate yourself", Toast.LENGTH_SHORT).show();
        }
    }

    // Modified fetchUsers method to take query
    private void fetchUsers(String query) {
        String url = "http://10.90.72.167:8080/users"; // Adjust to your backend URL

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        userList.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject userObject = response.getJSONObject(i);
                            String email = userObject.getString("emailId");
                            boolean isActive = userObject.getBoolean("ifActive");

                            // Only add users that match the search query
                            if (email.toLowerCase().contains(query.toLowerCase())) {
                                userList.add(new User(email, isActive)); // Include status in user list
                            }
                        }

                        // Notify the adapter to refresh the data
                        adminAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AdminActivity.this, "Failed to parse users", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(AdminActivity.this, "Failed to fetch users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add request to the queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    // Modified filterUsers method to update userList based on search query
    private void filterUsers(String query) {
        // Fetch users from the server with the query to update user list
        fetchUsers(query);
    }

    // Send a delete request to the backend
    private void sendDeleteRequest(String email) {
        String url = "http://10.90.72.167:8080/users/" + email; // Adjust the endpoint for delete

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        if ("success".equalsIgnoreCase(message)) {
                            Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                            // Optionally remove the user from the list and update UI
                            fetchUsers(""); // Refresh the list after deletion
                        } else {
                            Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error in deleting user", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error deleting user: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add request to the queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void sendBanRequest(String email, boolean b) {
        // Backend expects POST to "/users/{emailId}/status"
        String url = "http://10.90.72.167:8080/users/" + email + "/status";

        // Sending a POST request without additional parameters
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    try {
                        Log.d("BanUserResponse", response.toString());

                        String message = response.getString("message");
                        if ("User status toggled successfully".equalsIgnoreCase(message)) {
                            Toast.makeText(this, "User status toggled successfully", Toast.LENGTH_SHORT).show();
                            fetchUsers(""); // Refresh the list after banning/activating
                        } else {
                            Toast.makeText(this, "Failed to toggle user status", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error in toggling user status", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("BanUserError", error.toString());
                    Toast.makeText(this, "Error toggling user status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the request to the request queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}