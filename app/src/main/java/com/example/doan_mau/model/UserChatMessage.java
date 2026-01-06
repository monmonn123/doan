package com.example.doan_mau.model;

public class UserChatMessage {
    private String senderId;
    private String receiverId;
    private String content;
    private String createdAt;

    public UserChatMessage(String senderId, String receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
}