package com.example.book.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.book.AppController;
import com.example.book.adapter.MessageListAdapter;
import com.example.book.databinding.ActivityMessageListBinding;
import com.example.book.manager.UserManager;
import com.example.book.ui.Model.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class MessageListActivity extends AppCompatActivity {

    //region Attributes
    //region Class Constant
    private ActivityMessageListBinding actBinding;
    private Activity activity;
    private final String TAG = "MessageListActivity";
    //endregion Class Constant
    private MessageListAdapter messageListAdapter;
    private String username;
    //endregion Attributes

    //region Methods
    //region Initialization
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actBinding = ActivityMessageListBinding.inflate(getLayoutInflater());
        setContentView(actBinding.getRoot());
        activity = this;
        AppController.getInstance().setCurrentActivity(activity);

//        username = AppController.getInstance().getUser().getUsername();
        username = AppController.getInstance().getManager(UserManager.class).getUser().getUsername();

        List<ChatRoom> chatRooms = new ArrayList<>();
        chatRooms.addAll(AppController.getInstance().getChatRooms());
        Log.d(TAG, "username: " + username);
        for (ChatRoom chatroom: chatRooms) {
            Log.d(TAG, "chatroom: " + chatroom.getChatroomId());
        }
    
        messageListAdapter = new MessageListAdapter(this, username, chatRooms);
        actBinding.recChat.setLayoutManager(new LinearLayoutManager(this));
        actBinding.recChat.setAdapter(messageListAdapter);
        updateVisibility();
    }
    //endregion Initialization

    private void updateVisibility() {
        if (messageListAdapter.getItemCount() > 0) {
            actBinding.recChat.setVisibility(View.VISIBLE);
            actBinding.recEmpty.setVisibility(View.GONE);
        } else {
            actBinding.recChat.setVisibility(View.GONE);
            actBinding.recEmpty.setVisibility(View.VISIBLE);
        }
    }
    //endregion Methods

    //region Extras
    //endregion Extras

}