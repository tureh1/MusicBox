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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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

        selectedUsers = new HashSet<>();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
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
            public void afterTextChanged(Editable s) {}
        });

        searchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Show friendsList, createGroupButton, and chipGroup when search bar gains focus
                friendsList.setVisibility(View.VISIBLE);
                createGroupButton.setVisibility(View.VISIBLE);
                chipGroup.setVisibility(View.VISIBLE);
            } else {
                // Hide them when search bar loses focus, if it's empty
                if (searchBar.getText().toString().isEmpty()) {
                    friendsList.setVisibility(View.GONE);
                    createGroupButton.setVisibility(View.GONE);
                    chipGroup.setVisibility(View.GONE);
                }
            }
        });

        // Set up touch listener on root layout to hide friendsList and createGroupButton on whitespace click
        findViewById(R.id.create_group_activity_root).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_CANCEL) {
                friendsList.setVisibility(View.GONE);
                createGroupButton.setVisibility(View.GONE);
                chipGroup.setVisibility(View.GONE);
                searchBar.clearFocus();
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
        chipGroup = findViewById(R.id.selected_users_chip_group); // Initialize ChipGroup
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
        String url = "http://10.90.72.167:8080/users";

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

    private void toggleUserSelection(String userName) {
        if (selectedUsers.contains(userName)) {
            selectedUsers.remove(userName);
            removeChip(userName);
        } else {
            selectedUsers.add(userName);
            addChip(userName);
        }

    }



    private void addChip(String userName) {
        // Limit the number of selected users to 8
        if (selectedUsers.size() >= 8) {
            Toast.makeText(this, "You can select up to 8 users only", Toast.LENGTH_SHORT).show();
            return;
        }

        Chip chip = new Chip(this);
        chip.setText(userName);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> toggleUserSelection(userName));

        // Add the chip to the ChipGroup
        chipGroup.addView(chip);

        // Make sure the ChipGroup can wrap after 4 users by setting it to wrap its content

    }

    private void removeChip(String userName) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(userName)) {
                chipGroup.removeView(chip);
                break;
            }
        }
    }

    @Override
    public void onGroupClick(Group group) {}

    @Override
    public void onGroupDelete(Group group) {
        Log.d(TAG, "Deleting group: " + group.getName());
    }

    @Override
    public void onUserClick(String email) {
        if (!selectedUsers.contains(email)) {
            toggleUserSelection(email);
            searchBar.setText(""); // Clear the search bar text after clicking on a user
        }
        else{
            Toast.makeText(this, "User already selected", Toast.LENGTH_SHORT).show();
            searchBar.setText(""); // Clear the search bar text after clicking on a user
        }
    }
}