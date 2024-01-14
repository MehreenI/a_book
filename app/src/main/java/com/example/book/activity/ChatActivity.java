package com.example.book.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


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
    private String chatRoomId;
    ChatRoom chatRoom;

    public DatabaseReference DBChatRoomPath = FirebaseDatabase.getInstance().getReference("chatroom");

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
        Intent intent = getIntent();
        if (intent != null) {
            chatRoomId = intent.getStringExtra("ChatRoomId");
            if(chatRoomId == null){
                chatRoomId = chatRoom.getChatroomId();
            }
        }
        Log.d("ChatActivity", "onCreate: chatRoomId " + chatRoomId);
        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>()
                        .setQuery( DBChatRoomPath.child(chatRoomId).child("messages"), ChatMessage.class)
                        .build();

        chatMessageAdapter = new ChatMessageAdapter(this, AppController.getInstance().getManager(UserManager.class).getUser().getUsername(),options);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        actBinding.recChat.setLayoutManager(linearLayoutManager);
        actBinding.recChat.setAdapter(chatMessageAdapter);

        actBinding.btnSend.setOnClickListener(v -> {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(actBinding.txtMessage.getText().toString());
            Log.d("ChatActivity", "onCreate: chatMessage " + chatMessage);
            AppController.getInstance().getManager(FirebaseManager.class).sendChatMessage(chatRoom, chatMessage);
            actBinding.txtMessage.setText("");
            chatMessageAdapter.notifyDataSetChanged();
            linearLayoutManager.scrollToPosition(chatMessageAdapter.getItemCount());
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