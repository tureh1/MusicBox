package com.example.musibox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private final List<Friend> friendList;
    private final OnFriendDeleteListener deleteListener;
    private final OnFriendUpdateListener updateListener;

    public FriendsAdapter(List<Friend> friendList, OnFriendDeleteListener deleteListener, OnFriendUpdateListener updateListener) {
        this.friendList = friendList;
        this.deleteListener = deleteListener; // Initialize the delete listener
        this.updateListener = updateListener; // Initialize the update listener
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_view, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        holder.friendName.setText(friend.getFriendName());
        holder.friendEmail.setText(friend.getFriendEmail());

        // Set up the delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            deleteListener.onFriendDelete(friend); // Notify the listener for deletion
        });

        // Set up the update button click listener
        holder.updateButton.setOnClickListener(v -> {
            String newFriendName = holder.newFriendName.getText().toString();
            if (!newFriendName.isEmpty()) {
                updateListener.onFriendUpdate(friend.getFriendEmail(), newFriendName); // Notify the listener for update
            } else {
                Toast.makeText(holder.itemView.getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView friendEmail;
        Button deleteButton; // Declare the delete button
        Button updateButton; // Declare the update button
        EditText newFriendName; // Declare EditText for new friend's name

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            friendEmail = itemView.findViewById(R.id.friend_email);
            deleteButton = itemView.findViewById(R.id.delete_button); // Initialize the delete button
            updateButton = itemView.findViewById(R.id.update_button); // Initialize the update button
            newFriendName = itemView.findViewById(R.id.new_friend_name); // Initialize EditText for new name
        }
    }

    // Interface for delete listener
    public interface OnFriendDeleteListener {
        void onFriendDelete(Friend friend);
    }

    // Interface for update listener
    public interface OnFriendUpdateListener {
        void onFriendUpdate(String friendEmail, String newFriendName);
    }
}