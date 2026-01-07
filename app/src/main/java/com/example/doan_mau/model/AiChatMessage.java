package com.example.doan_mau.model;

public class AiChatMessage {
    private String content;
    private boolean isUser;

    public AiChatMessage(String content, boolean isUser) {
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
