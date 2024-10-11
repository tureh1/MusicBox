package com.example.musibox;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class FriendsActivity extends AppCompatActivity implements FriendsAdapter.OnFriendDeleteListener, FriendsAdapter.OnFriendUpdateListener {

    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private List<Friend> friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.friendsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        friendList = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(friendList, this, this); // Pass the update listener
        recyclerView.setAdapter(friendsAdapter);

        // Fetch the friends list from the backend
        fetchFriends();
    }

    private void fetchFriends() {
        // Retrieve user ID from SharedPreferences
        int userId = getSharedPreferences("user_data", MODE_PRIVATE).getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.90.72.167:8080/users/" + userId + "/friends";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Clear the list before adding new data
                        friendList.clear();

                        // Parse the response and add friends to the list
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject friendObject = response.getJSONObject(i);
                            String friendName = friendObject.getString("friendName");
                            String friendEmail = friendObject.getString("friendEmail");

                            friendList.add(new Friend(friendName, friendEmail));
                        }

                        // Notify the adapter of data change
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

        // Add the request to the Volley queue
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

        // Add the request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onFriendUpdate(String friendEmail, String newFriendName) {
        updateFriend(friendEmail, newFriendName);
    }

    private void updateFriend(String friendEmail, String newFriendName) {
        String url = "http://10.90.72.167:8080/friends/email/" + friendEmail;

        // Prepare JSON object for the PUT request
        JSONObject updatedFriendJson = new JSONObject();
        try {
            updatedFriendJson.put("friendName", newFriendName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, updatedFriendJson,
                response -> {
                    Toast.makeText(FriendsActivity.this, "Friend name updated", Toast.LENGTH_SHORT).show();
                    fetchFriends(); // Refresh the friends list
                },
                error -> {
                    Log.e("FriendsActivity", "Error: " + error.toString());
                    Toast.makeText(FriendsActivity.this, "Failed to update friend: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the request to the Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}