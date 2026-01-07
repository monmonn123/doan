package com.example.doan_mau;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan_mau.model.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
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
        Comment comment = comments.get(position);
        
        // Giả sử Comment có các phương thức getter phù hợp
        // holder.tvUser.setText(comment.getUserInfo());
        // holder.tvContent.setText(comment.getText());

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