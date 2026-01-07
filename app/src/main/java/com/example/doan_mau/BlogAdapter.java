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

import com.example.doan_mau.model.BlogPost;

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

        // --- KHÔI PHỤC HIỂN THỊ ---
        if (post.getUserId() != null) {
            holder.tvAuthor.setText(post.getUserId().getMssv() + " - " + post.getUserId().getHoTen());
        } else {
            holder.tvAuthor.setText("Ẩn danh");
        }
        holder.tvContent.setText(post.getContent());
        holder.tvLikeCount.setText(String.valueOf(post.getLikes().size()));
        holder.tvDislikeCount.setText(String.valueOf(post.getDislikes().size()));
        // --- KẾT THÚC KHÔI PHỤC ---

        if (isAdmin) {
            holder.layoutAdmin.setVisibility(View.VISIBLE);
            holder.layoutStudent.setVisibility(View.GONE);
        } else {
            holder.layoutAdmin.setVisibility(View.GONE);
            holder.layoutStudent.setVisibility(View.VISIBLE);
        }

        SharedPreferences prefs = context.getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);
        String currentUserId = prefs.getString("USER_ID", "");

        holder.btnLike.setOnClickListener(v -> toggleLike(post.getId(), currentUserId, holder.getAdapterPosition()));
        holder.btnDislike.setOnClickListener(v -> toggleDislike(post.getId(), currentUserId, holder.getAdapterPosition()));

        holder.btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("QUESTION_ID", post.getId());
            context.startActivity(intent);
        });

        holder.btnApprove.setOnClickListener(v -> handleAdminAction(post.getId(), "approved", holder.getAdapterPosition(), v));
        holder.btnReject.setOnClickListener(v -> handleAdminAction(post.getId(), "rejected", holder.getAdapterPosition(), v));
    }
    
    private void toggleLike(String postId, String userId, int position) {
        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        RetrofitClient.getApi().toggleLike(postId, body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    loadUpdatedPost(postId, position);
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
        });
    }

    private void toggleDislike(String postId, String userId, int position) {
        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        RetrofitClient.getApi().toggleDislike(postId, body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    loadUpdatedPost(postId, position);
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
            });
    }

    private void loadUpdatedPost(String postId, int position) {
        if (position == RecyclerView.NO_POSITION) return;
        RetrofitClient.getApi().getPostById(postId).enqueue(new Callback<BlogPost>() {
            @Override
            public void onResponse(Call<BlogPost> call, Response<BlogPost> response) {
                if(response.isSuccessful() && response.body() != null) {
                    list.set(position, response.body());
                    notifyItemChanged(position);
                }
            }
            @Override
            public void onFailure(Call<BlogPost> call, Throwable t) {}
        });
    }

    private void handleAdminAction(String id, String action, int pos, View v) {
        Map<String, String> body = new HashMap<>();
        body.put("action", action);
        RetrofitClient.getApi().updateStatus(id, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && pos != RecyclerView.NO_POSITION) {
                    list.remove(pos);
                    notifyItemRemoved(pos);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

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