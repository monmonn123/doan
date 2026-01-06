package com.example.doan_mau;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan_mau.model.Question;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questions;

    public QuestionAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.tvUserName.setText(question.getUserName());
        holder.tvQuestionContent.setText(question.getContent());
        holder.tvLikeCount.setText(String.valueOf(question.getLikes()));
        holder.tvDislikeCount.setText(String.valueOf(question.getDislikes()));

        holder.btnLike.setOnClickListener(v -> Toast.makeText(v.getContext(), "Liked", Toast.LENGTH_SHORT).show());
        holder.btnDislike.setOnClickListener(v -> Toast.makeText(v.getContext(), "Disliked", Toast.LENGTH_SHORT).show());
        holder.btnComment.setOnClickListener(v -> Toast.makeText(v.getContext(), "Mở bình luận", Toast.LENGTH_SHORT).show());
        holder.btnReport.setOnClickListener(v -> Toast.makeText(v.getContext(), "Báo cáo câu hỏi này", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvQuestionContent, tvLikeCount, tvDislikeCount;
        LinearLayout btnLike, btnDislike, btnComment;
        ImageView btnReport;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvQuestionContent = itemView.findViewById(R.id.tvQuestionContent);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvDislikeCount = itemView.findViewById(R.id.tvDislikeCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnDislike = itemView.findViewById(R.id.btnDislike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnReport = itemView.findViewById(R.id.btnReport);
        }
    }
}