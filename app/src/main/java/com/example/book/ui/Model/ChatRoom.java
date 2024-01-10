package com.example.book.ui.Model;

import com.example.book.AppController;
import com.example.book.manager.UserManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom implements Serializable {

    private String chatroomId;
    private List<String> userIds;
    private List<ChatMessage> messages;
    private long lastMessageTimestamp;
    private String postId;
    private String lastSenderId;
    private String lastMessage;

    public ChatRoom() {
        this.lastSenderId = AppController.getInstance().getManager(UserManager.class).getUser().getUsername();
        this.lastMessageTimestamp = AppController.getCurrentTimestamp();
        this.messages = new ArrayList<>();
        this.lastMessageTimestamp = 0;
        this.postId = "";
        this.lastMessage = "";
    }

    public ChatRoom(String chatroomId, List<String> userIds, List<ChatMessage> messages, long lastMessageTimestamp, String postId, String lastSenderId, String lastMessage) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.messages = messages;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.postId = postId;
        this.lastSenderId = lastSenderId;
        this.lastMessage = lastMessage;
    }

    public ChatRoom(String chatroomId, List<String> userIds, long lastMessageTimestamp, String lastSenderId, String lastMessage) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastSenderId = lastSenderId;
        this.lastMessage = lastMessage;
    }

    public String getChatroomId() {
        return chatroomId;
    }
    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }
    public List<String> getUserIds() {
        return userIds;
    }
    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }
    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
    public List<ChatMessage> getMessages() {
        return messages;
    }
    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }
    public String getLastSenderId() {
        return lastSenderId;
    }
    public void setLastSenderId(String lastMessageSenderId) {
        this.lastSenderId = lastMessageSenderId;
    }
    public String getLastMessage() {
        return lastMessage;
    }
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
