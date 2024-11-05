package com.example.musibox;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<Message> messageList;
    private final OnMessageClickListener listener;

    // Define an interface for click events
    public interface OnMessageClickListener {
        void onMessageClick(String friendEmail);
        void onFriendDelete(Message friend); // Add method for deleting a friend
    }

    public MessageAdapter(List<Message> messageList, OnMessageClickListener listener) {
        this.messageList = messageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_message_veiw, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message, listener);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView friendEmailView;
        private final ImageView optionsIcon;

        public MessageViewHolder(View itemView) {
            super(itemView);
            friendEmailView = itemView.findViewById(R.id.username);
            optionsIcon = itemView.findViewById(R.id.optionsIcon); // Reference to the options icon
        }

        public void bind(Message message, OnMessageClickListener listener) {
            friendEmailView.setText(message.getUsername());
            itemView.setOnClickListener(v -> listener.onMessageClick(message.getUsername()));

            optionsIcon.setOnClickListener(v -> {
                // Show a popup menu with the "Delete Friend" option
                PopupMenu popupMenu = new PopupMenu(v.getContext(), optionsIcon);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.friend_dropdown, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_delete_friend) {
                        listener.onFriendDelete(message); // Call onFriendDelete
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
        }
    }
}
