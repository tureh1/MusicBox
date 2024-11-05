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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
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
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.create_group_activity_root).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_CANCEL) {
                recyclerView.setVisibility(View.VISIBLE);
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
        Button createGroupButton = findViewById(R.id.create_group_button);
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
        } else {
            selectedUsers.add(userName);
        }
        updateSearchBarText();
    }

    private void updateSearchBarText() {
        StringBuilder usersText = new StringBuilder();
        for (String user : selectedUsers) {
            if (usersText.length() > 0) {
                usersText.append(", ");
            }
            usersText.append(user);
        }
        searchBar.setText(usersText.toString());
    }

    @Override
    public void onGroupClick(Group group) {}

    @Override
    public void onGroupDelete(Group group) {
        Log.d(TAG, "Deleting group: " + group.getName());
    }

    @Override
    public void onUserClick(String email) {
        toggleUserSelection(email);
    }
}
