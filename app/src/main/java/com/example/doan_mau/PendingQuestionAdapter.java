package com.example.doan_mau;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PendingQuestionAdapter extends RecyclerView.Adapter<PendingQuestionAdapter.ViewHolder> {

    private List<BlogPost> pendingQuestions;
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onApprove(String id);
        void onReject(String id);
    }

    public PendingQuestionAdapter(List<BlogPost> questions, OnActionClickListener listener) {
        this.pendingQuestions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BlogPost post = pendingQuestions.get(position);
        
        // Hiển thị nội dung từ database
        holder.tvContent.setText(post.getContent());
        
        // HIỂN THỊ MSSV - HỌ VÀ TÊN (SỬA LỖI COMPILATION)
        holder.tvUser.setText("Người gửi: " + post.getUserInfo());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(post.getId()));
        holder.btnReject.setOnClickListener(v -> listener.onReject(post.getId()));
    }

    @Override
    public int getItemCount() {
        return pendingQuestions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvContent;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvPendingUser);
            tvContent = itemView.findViewById(R.id.tvPendingContent);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}