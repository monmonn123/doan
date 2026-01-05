package com.example.doan_mau;

public class Question {
    private String id;
    private String authorName;
    private String content;
    private int likes;
    private int dislikes;
    private int commentsCount;

    public Question(String authorName, String content, int likes, int dislikes, int commentsCount) {
        this.authorName = authorName;
        this.content = content;
        this.likes = likes;
        this.dislikes = dislikes;
        this.commentsCount = commentsCount;
    }

    public String getAuthorName() { return authorName; }
    public String getContent() { return content; }
    public int getLikes() { return likes; }
    public int getDislikes() { return dislikes; }
    public int getCommentsCount() { return commentsCount; }
}
