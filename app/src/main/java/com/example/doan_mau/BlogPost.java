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
    public List<String> dislikes;

    public String getId() { return _id; }
    public String getContent() { return content; }

    // THÊM CÁC HÀM NÀY ĐỂ ADAPTER KHÔNG LỖI
    public int getLikesCount() { return likes != null ? likes.size() : 0; }
    public int getDislikesCount() { return dislikes != null ? dislikes.size() : 0; }
    public void setLikesCount(int count) { /* Cập nhật để UI nhảy số */ }
    public void setDislikesCount(int count) { /* Cập nhật để UI nhảy số */ }

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