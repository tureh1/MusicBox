package com.example.musibox;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> messageList;
    private final String userEmail;

    public ChatAdapter(List<ChatMessage> messageList, String userEmail) {
        this.messageList = messageList;
        this.userEmail = userEmail;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isSentByUser() ? 0 : 1; // 0 for outgoing, 1 for incoming
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outgoing_message, parent, false);
            return new OutgoingMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incoming_message, parent, false);
            return new IncomingMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = messageList.get(position);
        if (holder instanceof OutgoingMessageViewHolder) {
            ((OutgoingMessageViewHolder) holder).bind(chatMessage);
        } else if (holder instanceof IncomingMessageViewHolder) {
            ((IncomingMessageViewHolder) holder).bind(chatMessage);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class OutgoingMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView outgoingMessageText;

        OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            outgoingMessageText = itemView.findViewById(R.id.outgoingMessageText);
        }

        void bind(ChatMessage chatMessage) {
            outgoingMessageText.setText(chatMessage.getMessage());
        }
    }

    static class IncomingMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView incomingMessageText;

        IncomingMessageViewHolder(View itemView) {
            super(itemView);
            incomingMessageText = itemView.findViewById(R.id.incomingMessageText);
        }

        void bind(ChatMessage chatMessage) {
            incomingMessageText.setText(chatMessage.getMessage());
        }
    }
}
