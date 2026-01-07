package com.example.doan_mau.model;

public class UserChatMessage {
    private String content;
    private boolean isUser;

    public UserChatMessage(String content, boolean isUser) {
        this.content = content;
        this.isUser = isUser;
    }

    public String getContent() {
        return content;
    }

    public boolean isUser() {
        return isUser;
    }
}
