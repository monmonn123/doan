package com.example.doan_mau;

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
    private boolean isAdmin; // Dung dùng biến này để test giao diện Admin/SV

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
        holder.tvAuthor.setText(post.userId != null ? post.userId.username : "Sinh viên ẩn danh");
        holder.tvContent.setText(post.content);

        // HIỆN NÚT THEO VAI TRÒ
        if (isAdmin) {
            holder.layoutAdmin.setVisibility(View.VISIBLE);
            holder.layoutStudent.setVisibility(View.GONE);
        } else {
            holder.layoutAdmin.setVisibility(View.GONE);
            holder.layoutStudent.setVisibility(View.VISIBLE);
        }

        // LOGIC THẢ TIM
        holder.cbLike.setOnClickListener(v -> {
            Map<String, String> body = new HashMap<>();
            body.put("userId", "USER_ID_TEMP"); // Tạm thời để ID giả khi chưa có Login
            RetrofitClient.getApi().toggleLike(post._id, body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) Toast.makeText(v.getContext(), "Đã cập nhật Tim!", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        });

        // LOGIC ADMIN DUYỆT BÀI
        holder.btnApprove.setOnClickListener(v -> handleAdminAction(post._id, "approved", position, v));
        holder.btnReject.setOnClickListener(v -> handleAdminAction(post._id, "rejected", position, v));
    }

    private void handleAdminAction(String id, String action, int pos, View v) {
        Map<String, String> body = new HashMap<>();
        body.put("action", action);
        RetrofitClient.getApi().updateStatus(id, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                list.remove(pos); // Duyệt xong thì xóa khỏi danh sách chờ
                notifyItemRemoved(pos);
                Toast.makeText(v.getContext(), "Đã xử lý bài viết!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvContent, btnComment;
        CheckBox cbLike;
        Button btnApprove, btnReject;
        LinearLayout layoutAdmin, layoutStudent;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvAuthorName);
            tvContent = itemView.findViewById(R.id.tvContent);
            cbLike = itemView.findViewById(R.id.cbLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            layoutAdmin = itemView.findViewById(R.id.layoutAdminAction);
            layoutStudent = itemView.findViewById(R.id.layoutStudentAction);
        }
    }
}