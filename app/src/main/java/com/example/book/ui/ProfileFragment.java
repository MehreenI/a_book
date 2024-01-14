package com.example.book.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.book.AppController;
import com.example.book.R;
import com.example.book.activity.ChatActivity;
import com.example.book.activity.MessageListActivity;
import com.example.book.databinding.FragmentProfileBinding;
import com.example.book.manager.FirebaseManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding fragBinding;

    public ProfileFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragBinding = FragmentProfileBinding.inflate(inflater, container, false);

        fragBinding.btnMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MessageListActivity.class);
                Bundle bundle = new Bundle();
                String message = "Hello";
                bundle.putString("KEY_MESSAGE", message);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
//        fragBinding.btnSendMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("Message Button Pressed");
//                AppController.getInstance().getManager(FirebaseManager.class).isChatRoomCreated("dummyPost", "dummyBidder");
//
////                Intent intent = new Intent(getActivity(), ChatActivity.class);
////                Bundle bundle = new Bundle();
////                String message = "Hello";
////                bundle.putString("KEY_MESSAGE", message);
////                intent.putExtras(bundle);
//
////                startActivity(intent);
//            }
//        });

        return fragBinding.getRoot();
    }
}