package com.example.book.adapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.AppController;
import com.example.book.activity.ChatActivity;
import com.example.book.databinding.ItemmodelChatlistBinding;
import com.example.book.ui.Model.ChatRoom;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.CustomViewHolder> {

    private final Context context;
    private final String username;
    private final List<ChatRoom> chatRooms;

    public MessageListAdapter(Context context, String username, List<ChatRoom> chatRooms) {
        this.context = context;
        this.username = username;
        this.chatRooms = chatRooms;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(ItemmodelChatlistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        ChatRoom chatRoom = chatRooms.get(position);

        // Bind data to views
        holder.binding.txtUsername.setText(getOtherUser(chatRoom));
        holder.binding.txtLastmessage.setText(chatRoom.getLastMessage());

        // Set click listener
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click
                AppController.getInstance().setChatRoom(chatRoom);
                Intent intent = new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ChatRoomId", chatRoom.getChatroomId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    private String getOtherUser(ChatRoom chatRoom) {
        // Assuming the other user is the first one in the list
        List<String> userIds = chatRoom.getUserIds();
        if (userIds.size() > 0) {
            String otherUserId = userIds.get(0);
            if (otherUserId.equals(username)) {
                if (userIds.size() > 1) {
                    return userIds.get(1);
                }
            } else {
                return otherUserId;
            }
        }
        return "Unknown User";
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final ItemmodelChatlistBinding binding;

        public CustomViewHolder(ItemmodelChatlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
