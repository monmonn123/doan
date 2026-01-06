package com.example.doan_mau.model;

public class Comment {
    private String id;
    private String userName;
    private String content;
    private int likes;
    private int dislikes;

    public Comment(String id, String userName, String content, int likes, int dislikes) {
        this.id = id;
        this.userName = userName;
        this.content = content;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public String getId() { return id; }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public int getLikes() { return likes; }
    public int getDislikes() { return dislikes; }
}