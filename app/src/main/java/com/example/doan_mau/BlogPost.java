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
    public List<String> dislikes; // THÊM TRƯỜNG DISLIKES

    public String getId() { return _id; }
    public String getContent() { return content; }
    
    public String getUserInfo() {
        if (userId != null) {
            String mssv = (userId.mssv != null) ? userId.mssv : "N/A";
            String name = (userId.hoTen != null) ? userId.hoTen : "Ẩn danh";
            return mssv + " - " + name;
        }
        return "N/A";
    }

    public static class UserObj {
        public String _id;
        public String username;
        public String hoTen;
        public String mssv;
    }

    public static class CommentObj {
        public String text;
        public UserObj userId;
    }
}