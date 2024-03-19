package com.example.book.ui.Model;

import com.example.book.AppController;
import com.example.book.manager.UserManager;

public class ChatMessage {

    //region Attributes
    private String senderId;
    private String message;
    private long timestamp;
    private boolean isDelivered;
    private boolean isSent;
    private boolean isRead;
    //endregion Attributes

    
    
    //region Methods
    public ChatMessage() {
        this.senderId = AppController.getInstance().getManager(UserManager.class).getUser().getUsername();
        this.timestamp = AppController.getCurrentTimestamp();
        this.isDelivered = false;
        this.isSent = false;
        this.isRead = false;
    }
    public ChatMessage(String senderId, String message, long timestamp, boolean isDelivered, boolean isSent, boolean isRead) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.isDelivered = isDelivered;
        this.isSent = isSent;
        this.isRead = isRead;
    }
    //endregion Methods

    //region Getter/Setter
    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public boolean isDelivered() {
        return isDelivered;
    }
    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }
    public boolean isSent() {
        return isSent;
    }
    public void setSent(boolean sent) {
        isSent = sent;
    }
    public boolean isRead() {
        return isRead;
    }
    public void setRead(boolean read) {
        isRead = read;
    }
    //endregion Getter/Setter
}
