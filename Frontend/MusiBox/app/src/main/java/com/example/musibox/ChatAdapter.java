package com.example.musibox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_INCOMING = 1;
    private static final int VIEW_TYPE_OUTGOING = 2;
    private final List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        return message.isSentByUser() ? VIEW_TYPE_OUTGOING : VIEW_TYPE_INCOMING;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_INCOMING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.incoming_message, parent, false);
            return new IncomingMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.outgoing_message, parent, false);
            return new OutgoingMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder instanceof IncomingMessageViewHolder) {
            ((IncomingMessageViewHolder) holder).bind(message);
        } else {
            ((OutgoingMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class IncomingMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        IncomingMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.incomingMessageText);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getText());
        }
    }

    static class OutgoingMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.outgoingMessageText);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getText());
        }
    }
}
