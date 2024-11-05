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

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private final List<Group> groupList;
    private final OnGroupClickListener listener;

    // Define an interface for click events
    public interface OnGroupClickListener {
        void onGroupClick(Group group);
        void onGroupDelete(Group group); // Add method for deleting a group
    }

    public GroupAdapter(List<Group> groupList, OnGroupClickListener listener) {
        this.groupList = groupList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activitity_group_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.bind(group, listener);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView groupNameTextView;
        private final ImageView optionsIcon;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.username); // Ensure this matches your layout
            optionsIcon = itemView.findViewById(R.id.optionsIcon); // Reference to the options icon
        }

        public void bind(Group group, OnGroupClickListener listener) {
            groupNameTextView.setText(group.getName());

            // Set click listener for the item view
            itemView.setOnClickListener(v -> listener.onGroupClick(group));


        }
    }
}