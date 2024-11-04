package com.example.musibox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> messageList;
    private static final int VIEW_TYPE_USER_MESSAGE = 1;
    private static final int VIEW_TYPE_FRIEND_MESSAGE = 2;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }


    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        return message.isSentByUser() ? VIEW_TYPE_USER_MESSAGE : VIEW_TYPE_FRIEND_MESSAGE;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.outgoing_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.incoming_message, parent, false);
            return new FriendMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message.getContent(), message.getTimestamp());
        } else if (holder instanceof FriendMessageViewHolder) {
            ((FriendMessageViewHolder) holder).bind(message.getContent(), message.getTimestamp());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;

        public UserMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.outgoingMessageText);
            timestampTextView = itemView.findViewById(R.id.outgoingMessageTimestamp);
        }

        public void bind(String message, String timestamp) {
            messageTextView.setText(message);
            timestampTextView.setText(timestamp);
        }
    }

    public static class FriendMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;

        public FriendMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.incomingMessageText);
            timestampTextView = itemView.findViewById(R.id.incomingMessageTimestamp);
        }

        public void bind(String message, String timestamp) {
            messageTextView.setText(message);
            timestampTextView.setText(timestamp);
        }
    }
}
