package com.example.doan_mau;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<Question> questionList;

    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.tvAuthorName.setText(question.getAuthorName());
        holder.tvQuestionContent.setText(question.getContent());
        holder.tvLikes.setText(String.valueOf(question.getLikes()));
        holder.tvDislikes.setText(String.valueOf(question.getDislikes()));
        holder.tvComments.setText(question.getCommentsCount() + " Bình luận");
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthorName, tvQuestionContent, tvLikes, tvDislikes, tvComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvQuestionContent = itemView.findViewById(R.id.tvQuestionContent);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvDislikes = itemView.findViewById(R.id.tvDislikes);
            tvComments = itemView.findViewById(R.id.tvComments);
        }
    }
}
