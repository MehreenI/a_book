package com.example.book.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book.AppController;
import com.example.book.adapter.ChatMessageAdapter;
import com.example.book.databinding.ActivityChatBinding;
import com.example.book.manager.FirebaseManager;
import com.example.book.manager.UserManager;
import com.example.book.ui.Model.ChatMessage;
import com.example.book.ui.Model.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {
    
    //region Attributes
    //region Class Constant
    private ActivityChatBinding actBinding;
    private Activity activity;
    private final String TAG = "ChatActivity";
    //endregion Class Constant
    private ChatMessageAdapter chatMessageAdapter;
    ChatRoom chatRoom;
    //endregion Attributes
    
    //region Methods
    //region Initialization
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(actBinding.getRoot());
        activity = this;
        AppController.getInstance().setCurrentActivity(activity);
        
        chatRoom = AppController.getInstance().getChatRoom();
        
        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>()
                        .setQuery(AppController.getInstance().getManager(FirebaseManager.class).showChatMessage(chatRoom.getChatroomId()), ChatMessage.class)
                        .build();
        
        
        chatMessageAdapter = new ChatMessageAdapter(this, AppController.getInstance().getManager(UserManager.class).getUser().getUsername(),options);
        actBinding.recChat.setLayoutManager(new LinearLayoutManager(this));
        actBinding.recChat.setAdapter(chatMessageAdapter);
        
        actBinding.btnSend.setOnClickListener(v -> {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(actBinding.txtMessage.getText().toString());
            AppController.getInstance().getManager(FirebaseManager.class).sendChatMessage(chatRoom, chatMessage);
            actBinding.txtMessage.setText("");
            chatMessageAdapter.notifyDataSetChanged();
        });
    }
    //endregion Initialization
    //endregion Methods
    
    //region Extras
    @Override
    protected void onStart() {
        super.onStart();
        chatMessageAdapter.startListening();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        chatMessageAdapter.stopListening();
    }
    //endregion Extras
    
}