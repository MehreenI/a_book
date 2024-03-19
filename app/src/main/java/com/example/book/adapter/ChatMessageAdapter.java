package com.example.book.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book.AppController;
import com.example.book.databinding.ItemmodelChatmessageBinding;
import com.example.book.ui.Model.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ChatMessageAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatMessageAdapter.CustomViewHolder> {

    private final String username;
    private String TAG ="chat";
    private Context context;

    public ChatMessageAdapter(Context context, String username, @NonNull FirebaseRecyclerOptions<ChatMessage> options) {
        super(options);
        this.context = context;
        this.username = username;
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomViewHolder holder, int position, @NonNull ChatMessage model) {
        if (model.getSenderId().equals(username))
        {
            Log.d(TAG, "onBindViewHolder:  "+username);
            showYourMessage(holder, model);
        } else {
            Log.d(TAG, "onBindViewHolder:  "+username);

            showOtherMessage(holder, model);
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemmodelChatmessageBinding binding = ItemmodelChatmessageBinding.inflate(layoutInflater, parent, false);
        return new CustomViewHolder(binding);
    }

    private void showYourMessage(@NonNull CustomViewHolder holder, @NonNull ChatMessage model) {
        holder.binding.sideMessageOther.setVisibility(View.GONE);
        holder.binding.sideMessageYou.setVisibility(View.VISIBLE);

        holder.binding.txtMessage.setText(model.getMessage());
        holder.binding.txtTime.setText(AppController.getRelativeTime(model.getTimestamp()));

        // Set visibility of message status indicators
        if (model.isRead()) {
            holder.binding.imgSeen.setVisibility(View.VISIBLE);
            holder.binding.imgDelivered.setVisibility(View.GONE);
            holder.binding.imgSent.setVisibility(View.GONE);
        } else if (model.isDelivered()) {
            holder.binding.imgSeen.setVisibility(View.GONE);
            holder.binding.imgDelivered.setVisibility(View.VISIBLE);
            holder.binding.imgSent.setVisibility(View.GONE);
        } else if (model.isSent()) {
            holder.binding.imgSeen.setVisibility(View.GONE);
            holder.binding.imgDelivered.setVisibility(View.GONE);
            holder.binding.imgSent.setVisibility(View.VISIBLE);
        } else {
            holder.binding.imgSeen.setVisibility(View.GONE);
            holder.binding.imgDelivered.setVisibility(View.GONE);
            holder.binding.imgSent.setVisibility(View.GONE);
        }
    }

    private void showOtherMessage(@NonNull CustomViewHolder holder, @NonNull ChatMessage model) {
        holder.binding.sideMessageOther.setVisibility(View.VISIBLE);
        holder.binding.sideMessageYou.setVisibility(View.GONE);

        holder.binding.txtMessageO.setText(model.getMessage());
        holder.binding.txtTimeO.setText(AppController.getRelativeTime(model.getTimestamp()));
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final ItemmodelChatmessageBinding binding;

        public CustomViewHolder(ItemmodelChatmessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
