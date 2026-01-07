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

        // Hiển thị số lượng tương tác ban đầu
        updateInteractionCounts(holder, post);

        // Phân quyền hiển thị nút Admin/Student
        if (isAdmin) {
            holder.layoutAdmin.setVisibility(View.VISIBLE);
            holder.layoutStudent.setVisibility(View.GONE);
        } else {
            holder.layoutAdmin.setVisibility(View.GONE);
            holder.layoutStudent.setVisibility(View.VISIBLE);
        }

        SharedPreferences prefs = context.getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);
        String currentUserId = prefs.getString("USER_ID", "");

        // 1. XỬ LÝ NÚT LIKE (KIỂU FACEBOOK)
        // Trong onBindViewHolder của BlogAdapter.java
        holder.btnLike.setOnClickListener(v -> {
            Map<String, String> body = new HashMap<>();
            body.put("userId", currentUserId);

            // FIX LỖI TẠI ĐÂY: Callback phải khớp với Map<String, Object>
            RetrofitClient.getApi().toggleLike(post._id, body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Ép kiểu Double về int để cập nhật UI
                        int likes = ((Double) response.body().get("likesCount")).intValue();
                        int dislikes = ((Double) response.body().get("dislikesCount")).intValue();

                        // Cập nhật và nhảy số ngay lập tức
                        post.likes = new ArrayList<>(Collections.nCopies(likes, ""));
                        post.dislikes = new ArrayList<>(Collections.nCopies(dislikes, ""));
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
            });
        });
        // 2. XỬ LÝ NÚT DISLIKE
        holder.btnDislike.setOnClickListener(v -> {
            if (currentUserId.isEmpty()) return;
            Map<String, String> body = new HashMap<>();
            body.put("userId", currentUserId);

            // SỬA LỖI TƯƠNG TỰ CHO DISLIKE
            RetrofitClient.getApi().toggleDislike(post._id, body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        int likesCount = ((Double) response.body().get("likesCount")).intValue();
                        int dislikesCount = ((Double) response.body().get("dislikesCount")).intValue();

                        post.setLikesCount(likesCount);
                        post.setDislikesCount(dislikesCount);
                        notifyItemChanged(holder.getAdapterPosition());
                        Toast.makeText(context, "Đã cập nhật Không thích", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
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

        // 5. CLICK ON AUTHOR TO VIEW PROFILE
        holder.tvAuthor.setOnClickListener(v -> {
            if (post.userId != null && post.userId._id != null) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("USER_ID", post.userId._id);
                context.startActivity(intent);
            }
        });

        holder.tvAuthor.setOnLongClickListener(v -> {
            if (post.userId != null && post.userId._id != null) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("USER_ID", post.userId._id);
                context.startActivity(intent);
            }
            return true;
        });

        // 6. ADMIN ACTION (DUYỆT/HỦY BÀI)
        holder.btnApprove.setOnClickListener(v -> handleAdminAction(post._id, "approved", holder.getAdapterPosition(), v));
        holder.btnReject.setOnClickListener(v -> handleAdminAction(post._id, "rejected", holder.getAdapterPosition(), v));
    }

    private void updateInteractionCounts(BlogViewHolder holder, BlogPost post) {
        // Sử dụng hàm getLikesCount/getDislikesCount để đảm bảo lấy dữ liệu mới nhất
        holder.tvLikeCount.setText(String.valueOf(post.getLikesCount()));
        holder.tvDislikeCount.setText(String.valueOf(post.getDislikesCount()));
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
                    if (pos != RecyclerView.NO_POSITION) {
                        list.remove(pos);
                        notifyItemRemoved(pos);
                        Toast.makeText(v.getContext(), "Đã xử lý bài viết!", Toast.LENGTH_SHORT).show();
                    }
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