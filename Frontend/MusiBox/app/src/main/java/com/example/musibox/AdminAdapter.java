package com.example.musibox;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {
    private final List<User> userList;
    private final OnUserClickListener listener;

    // Define an interface for click events
    public interface OnUserClickListener {
        void onUserDelete(User user); // Handle user delete action
        void onUserBan(User user); // Handle user ban action

        // This method is triggered when the "Ban/Activate" option is clicked
        void onUserBanActivate(User user);
    }

    public AdminAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_users, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameView;
        private final TextView messageView;
        private final ImageView optionsIcon;

        public AdminViewHolder(View itemView) {
            super(itemView);
            usernameView = itemView.findViewById(R.id.username);
            messageView = itemView.findViewById(R.id.message);
            optionsIcon = itemView.findViewById(R.id.optionsIcon); // Reference to the options icon
        }

        public void bind(User user, OnUserClickListener listener) {
            usernameView.setText(user.getEmailId());

            optionsIcon.setOnClickListener(v -> {
                // Show a popup menu with the "Delete" and "Ban" options
                PopupMenu popupMenu = new PopupMenu(v.getContext(), optionsIcon);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.user_dropdown, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.delete_user) {
                        listener.onUserDelete(user); // Call onUserDelete
                        return true;
                    } else if (item.getItemId() == R.id.ban_user) {
                        listener.onUserBan(user); // Call onUserBan
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
        }
    }
}