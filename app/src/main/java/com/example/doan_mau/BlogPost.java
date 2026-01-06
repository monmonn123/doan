package com.example.doan_mau;

import java.util.List;

public class BlogPost {
    public String _id;
    public String content;
    public String image;
    public String status;
    public UserObj userId;
    public List<CommentObj> comments;
    public List<String> likes;

    public static class UserObj { public String username; }
    public static class CommentObj {
        public String text;
        public UserObj userId;
    }
}