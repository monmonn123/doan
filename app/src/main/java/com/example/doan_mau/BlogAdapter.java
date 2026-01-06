package com.example.doan_mau;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private List<BlogPost> list;
    private boolean isAdmin;

    public BlogAdapter(List<BlogPost> list, boolean isAdmin) {
        this.list = list;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog_post, parent, false);
        return new BlogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogPost post = list.get(position);
        Context context = holder.itemView.getContext();
        
        holder.tvAuthor.setText(post.getUserInfo());
        holder.tvContent.setText(post.content);
        
        // Hiển thị số lượng tương tác
        updateInteractionCounts(holder, post);

        if (isAdmin) {
            holder.layoutAdmin.setVisibility(View.VISIBLE);
            holder.layoutStudent.setVisibility(View.GONE);
        } else {
            holder.layoutAdmin.setVisibility(View.GONE);
            holder.layoutStudent.setVisibility(View.VISIBLE);
        }

        SharedPreferences prefs = context.getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);
        String currentUserId = prefs.getString("USER_ID", "");

        // 1. LIKE
        holder.btnLike.setOnClickListener(v -> {
            if (currentUserId.isEmpty()) return;
            Map<String, String> body = new HashMap<>();
            body.put("userId", currentUserId);
            
            RetrofitClient.getApi().toggleLike(post._id, body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (post.likes == null) post.likes = new ArrayList<>();
                        if (post.dislikes == null) post.dislikes = new ArrayList<>();

                        if (post.likes.contains(currentUserId)) {
                            post.likes.remove(currentUserId);
                        } else {
                            post.likes.add(currentUserId);
                            post.dislikes.remove(currentUserId); // Bỏ dislike nếu đang like
                        }
                        updateInteractionCounts(holder, post);
                        Toast.makeText(context, "Đã cập nhật Thích", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        });

        // 2. DISLIKE
        holder.btnDislike.setOnClickListener(v -> {
            if (currentUserId.isEmpty()) return;
            Map<String, String> body = new HashMap<>();
            body.put("userId", currentUserId);
            
            RetrofitClient.getApi().toggleDislike(post._id, body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (post.dislikes == null) post.dislikes = new ArrayList<>();
                        if (post.likes == null) post.likes = new ArrayList<>();

                        if (post.dislikes.contains(currentUserId)) {
                            post.dislikes.remove(currentUserId);
                        } else {
                            post.dislikes.add(currentUserId);
                            post.likes.remove(currentUserId); // Bỏ like nếu đang dislike
                        }
                        updateInteractionCounts(holder, post);
                        Toast.makeText(context, "Đã cập nhật Không thích", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        });

        // 3. COMMENT (CÂU TRẢ LỜI)
        holder.btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("QUESTION_ID", post._id);
            context.startActivity(intent);
        });

        // 4. REPORT (BÁO CÁO)
        holder.btnReport.setOnClickListener(v -> showReportDialog(post._id, v));

        holder.btnApprove.setOnClickListener(v -> handleAdminAction(post._id, "approved", position, v));
        holder.btnReject.setOnClickListener(v -> handleAdminAction(post._id, "rejected", position, v));
    }

    private void updateInteractionCounts(BlogViewHolder holder, BlogPost post) {
        int likeCount = post.likes != null ? post.likes.size() : 0;
        int dislikeCount = post.dislikes != null ? post.dislikes.size() : 0;
        holder.tvLikeCount.setText(String.valueOf(likeCount));
        holder.tvDislikeCount.setText(String.valueOf(dislikeCount));
    }

    private void showReportDialog(String questionId, View v) {
        String[] reasons = {"Nội dung quấy rối", "Thông tin sai sự thật", "Spam", "Ngôn từ gây thù ghét", "Khác"};
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Báo cáo câu hỏi này");
        builder.setItems(reasons, (dialog, which) -> {
            String selectedReason = reasons[which];
            Toast.makeText(v.getContext(), "Đã gửi báo cáo: " + selectedReason, Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void handleAdminAction(String id, String action, int pos, View v) {
        Map<String, String> body = new HashMap<>();
        body.put("action", action);
        RetrofitClient.getApi().updateStatus(id, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    list.remove(pos);
                    notifyItemRemoved(pos);
                    Toast.makeText(v.getContext(), "Đã xử lý bài viết!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvContent, tvLikeCount, tvDislikeCount;
        LinearLayout btnLike, btnDislike, btnComment;
        ImageView btnReport;
        Button btnApprove, btnReject;
        LinearLayout layoutAdmin, layoutStudent;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvAuthorName);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvDislikeCount = itemView.findViewById(R.id.tvDislikeCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnDislike = itemView.findViewById(R.id.btnDislike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnReport = itemView.findViewById(R.id.btnReport);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            layoutAdmin = itemView.findViewById(R.id.layoutAdminAction);
            layoutStudent = itemView.findViewById(R.id.layoutStudentAction);
        }
    }
}