package com.example.book.adapter;

import android.content.Context;
import android.content.Intent;
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

    public Context context;
    String username;

    public ChatMessageAdapter(Context context, String username, @NonNull FirebaseRecyclerOptions<ChatMessage> options) {
        super(options);
        this.context = context;
        this.username = username;
    }
    @Override
    protected void onBindViewHolder(@NonNull CustomViewHolder holder, int position, @NonNull ChatMessage model) {
        RedrawView(holder,position,model);
    }
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(ItemmodelChatmessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }
    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public void RedrawView(@NonNull CustomViewHolder holder, int position, @NonNull ChatMessage model){

        Log.d("sendChatMessage", "RedrawView: getSenderId " + model.getSenderId());
        Log.d("sendChatMessage", "RedrawView: username " + username);
        if(model.getSenderId().equals(username))
        {
            Log.d("sendChatMessage", "RedrawView: your message");
            showYourMessage(holder, position, model);
        }else
        {
            Log.d("sendChatMessage", "RedrawView: other message");
            showOtherMessage(holder, position, model);
        }
    }

    public void showYourMessage(@NonNull CustomViewHolder holder, int position, @NonNull ChatMessage model){
        holder.binding.sideMessageOther.setVisibility(View.GONE);
        holder.binding.sideMessageYou.setVisibility(View.VISIBLE);

        holder.binding.txtMessage.setText(model.getMessage());
        holder.binding.txtTime.setText(AppController.getRelativeTime(model.getTimestamp()));

        if(model.isRead()) {
            holder.binding.imgSeen.setVisibility(View.VISIBLE);
            holder.binding.imgDelivered.setVisibility(View.GONE);
            holder.binding.imgSent.setVisibility(View.GONE);
        } else if (model.isDelivered()) {
            holder.binding.imgSeen.setVisibility(View.GONE);
            holder.binding.imgDelivered.setVisibility(View.VISIBLE);
            holder.binding.imgSent.setVisibility(View.GONE);
        } else if (model.isSent()){
            holder.binding.imgSeen.setVisibility(View.GONE);
            holder.binding.imgDelivered.setVisibility(View.GONE);
            holder.binding.imgSent.setVisibility(View.VISIBLE);
        } else {
            holder.binding.imgSeen.setVisibility(View.GONE);
            holder.binding.imgDelivered.setVisibility(View.GONE);
            holder.binding.imgSent.setVisibility(View.GONE);
        }
    }

    public void showOtherMessage(@NonNull CustomViewHolder holder, int position, @NonNull ChatMessage model){
        holder.binding.sideMessageOther.setVisibility(View.VISIBLE);
        holder.binding.sideMessageYou.setVisibility(View.GONE);

        holder.binding.txtMessageO.setText(model.getMessage());
        holder.binding.txtTimeO.setText(AppController.getRelativeTime(model.getTimestamp()));
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ItemmodelChatmessageBinding binding;
        public CustomViewHolder(ItemmodelChatmessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
