package com.example.doan_mau;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<BlogPost.CommentObj> comments;

    public CommentAdapter(List<BlogPost.CommentObj> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        BlogPost.CommentObj comment = comments.get(position);
        
        String userInfo = "Ẩn danh";
        if (comment.userId != null) {
            String mssv = (comment.userId.mssv != null) ? comment.userId.mssv : "N/A";
            String name = (comment.userId.hoTen != null) ? comment.userId.hoTen : (comment.userId.username != null ? comment.userId.username : "Sinh viên");
            userInfo = mssv + " - " + name;
        }
        
        holder.tvUser.setText(userInfo);
        holder.tvContent.setText(comment.text);

        holder.btnLike.setOnClickListener(v -> Toast.makeText(v.getContext(), "Đã thích bình luận", Toast.LENGTH_SHORT).show());
        holder.btnDislike.setOnClickListener(v -> Toast.makeText(v.getContext(), "Không thích bình luận", Toast.LENGTH_SHORT).show());
        holder.btnReport.setOnClickListener(v -> Toast.makeText(v.getContext(), "Đã báo cáo bình luận này", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvContent, btnReport;
        LinearLayout btnLike, btnDislike;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvCommentUser);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            btnLike = itemView.findViewById(R.id.btnLikeComment);
            btnDislike = itemView.findViewById(R.id.btnDislikeComment);
            btnReport = itemView.findViewById(R.id.btnReportComment);
        }
    }
}