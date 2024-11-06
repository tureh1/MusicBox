package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity implements GroupAdapter.OnGroupClickListener, UserAdapter.OnUserClickListener {
    private static final String TAG = "CreateGroupActivity";
    private RecyclerView recyclerView;
    private AutoCompleteTextView searchBar;
    private HashSet<String> selectedUsers;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;
    private ImageButton house, addUserButton, messageButton, userButton;
    private RecyclerView friendsList;
    private List<User> userList;
    private UserAdapter userAdapter;
    private Button createGroupButton;
    private ChipGroup chipGroup;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initViews();
        setupRecyclerView();
        setupNavigationButtons();

        fetchGroups();
        selectedUsers = new HashSet<>();

        createGroupButton.setOnClickListener(v -> {
            // Handle create group functionality
            createGroup();
            // Show the group list again after creating a group
            recyclerView.setVisibility(View.VISIBLE);
            friendsList.setVisibility(View.GONE);
            createGroupButton.setVisibility(View.GONE);
            chipGroup.setVisibility(View.GONE);
            chipGroup.removeAllViews();
            selectedUsers.clear();
            searchBar.getText().clear();
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // Fetch users based on search query
                    fetchUsers(s.toString());
                } else {
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                    friendsList.setVisibility(View.GONE);
                    createGroupButton.setVisibility(View.VISIBLE);
                    chipGroup.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Hide group list when search bar is focused
        searchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                recyclerView.setVisibility(View.GONE); // Hide group list when searching
                friendsList.setVisibility(View.VISIBLE);
                createGroupButton.setVisibility(View.VISIBLE);
                chipGroup.setVisibility(View.VISIBLE);
            } else {
                if (searchBar.getText().toString().isEmpty()) {
                    // Show the group list again if search bar is not focused and empty
                    recyclerView.setVisibility(View.VISIBLE);
                    friendsList.setVisibility(View.GONE);
                    createGroupButton.setVisibility(View.GONE);
                    chipGroup.setVisibility(View.GONE);
                }
            }
        });

        // Handle clicks outside the search bar (on white space)
        findViewById(R.id.create_group_activity_root).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // If the search bar is focused and the user taps outside, hide search results and show group list again
                if (searchBar.hasFocus()) {
                    friendsList.setVisibility(View.GONE);
                    createGroupButton.setVisibility(View.GONE);
                    chipGroup.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE); // Show group list again
                    searchBar.clearFocus(); // Clear focus from search bar
                }
            }
            return true;
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
        createGroupButton = findViewById(R.id.create_group_button);
        chipGroup = findViewById(R.id.selected_users_chip_group);
    }

    private void setupRecyclerView() {
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, this);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        friendsList.setLayoutManager(new LinearLayoutManager(this));
        friendsList.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(groupAdapter);
    }

    private void setupNavigationButtons() {
        house.setOnClickListener(v -> startActivity(new Intent(CreateGroupActivity.this, MainPage.class)));
        addUserButton.setOnClickListener(v -> startActivity(new Intent(CreateGroupActivity.this, CreateGroupActivity.class)));
        messageButton.setOnClickListener(v -> startActivity(new Intent(CreateGroupActivity.this, MessageActivity.class)));
        userButton.setOnClickListener(v -> startActivity(new Intent(CreateGroupActivity.this, UserProfileActivity.class)));
    }

    private void fetchUsers(String query) {
        int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.90.72.167:8080/users"; // Adjust the backend URL if needed

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        userList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject userObject = response.getJSONObject(i);
                            String email = userObject.getString("emailId");

                            if (email.toLowerCase().startsWith(query.toLowerCase())) {
                                userList.add(new User(email));
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                        friendsList.setVisibility(userList.isEmpty() ? View.GONE : View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        createGroupButton.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse users", e);
                        Toast.makeText(CreateGroupActivity.this, "Failed to parse users", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Failed to fetch users: " + error.getMessage());
                    Toast.makeText(CreateGroupActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void createGroup() {
        if (selectedUsers.isEmpty()) {
            Toast.makeText(this, "Please select at least one user", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate the group name by joining selected user names with commas
        String groupName = String.join(", ", selectedUsers);

        List<String> selectedUserEmails = new ArrayList<>(selectedUsers);

        // Check if the user is logged in
        int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construct the JSON request payload
        JSONObject groupData = new JSONObject();
        try {
            groupData.put("name", groupName); // Group name
            groupData.put("users", new JSONArray(selectedUserEmails)); // Selected users' emails
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = "http://10.90.72.167:8080/users/" + userId + "/playlists"; // Adjust URL if needed

        // Make the POST request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, groupData,
                response -> {
                    try {
                        // Assuming your backend returns a "message" field with the status
                        String message = response.getString("message");
                        Toast.makeText(CreateGroupActivity.this, message, Toast.LENGTH_SHORT).show();

                        // After group creation, refresh the groups list
                        fetchGroups();  // This will reload the groups

                        // Reset the UI
                        recyclerView.setVisibility(View.VISIBLE);
                        friendsList.setVisibility(View.GONE);
                        createGroupButton.setVisibility(View.GONE);
                        chipGroup.setVisibility(View.GONE);
                        chipGroup.removeAllViews();
                        selectedUsers.clear();
                        searchBar.getText().clear();
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse group creation response", e);
                        Toast.makeText(CreateGroupActivity.this, "Unexpected response from server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Failed to create group: " + error.getMessage());
                    Toast.makeText(CreateGroupActivity.this, "Failed to create group", Toast.LENGTH_SHORT).show();
                });

        // Add request to the Volley request queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    private void fetchGroups() {
        int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.90.72.167:8080/users/" + userId + "/playlists/myPlaylists"; // Adjust URL if needed

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        groupList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject groupObject = response.getJSONObject(i);
                            String groupName = groupObject.getString("name");
                            Group group = new Group(groupName);
                            groupList.add(group);
                        }
                        groupAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse groups", e);
                        Toast.makeText(CreateGroupActivity.this, "Failed to load groups", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Failed to fetch groups: " + error.getMessage());
                    Toast.makeText(CreateGroupActivity.this, "Failed to fetch groups", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }



    @Override
    public void onUserClick(String email) {
        if (selectedUsers.add(email)) {
            Chip chip = new Chip(this);
            chip.setText(email);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                selectedUsers.remove(email);
                chipGroup.removeView(chip);
            });
            chipGroup.addView(chip);
            searchBar.getText().clear();
        }
    }




    @Override
    public void onGroupClick(Group group) {
        // Start PlaylistActivity when a group is clicked
        Intent intent = new Intent(CreateGroupActivity.this, PlaylistActivity.class);
        intent.putExtra("groupName", group.getName()); // Pass group name as an extra
        startActivity(intent);
    }

    @Override
    public void onGroupDelete(Group group) {
        // Handle group delete
        Toast.makeText(this, "Group deleted: " + group.getName(), Toast.LENGTH_SHORT).show();
    }
}
