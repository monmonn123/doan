package com.example.doan_mau;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan_mau.model.AiChatMessage;
import java.util.List;

public class AiChatAdapter extends RecyclerView.Adapter<AiChatAdapter.AiViewHolder> {

    private List<AiChatMessage> messages;

    public AiChatAdapter(List<AiChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public AiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai_message, parent, false);
        return new AiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AiViewHolder holder, int position) {
        AiChatMessage message = messages.get(position);
        if (message.isUser()) {
            holder.layoutUser.setVisibility(View.VISIBLE);
            holder.layoutAi.setVisibility(View.GONE);
            holder.tvUserMessage.setText(message.getContent());
        } else {
            holder.layoutAi.setVisibility(View.VISIBLE);
            holder.layoutUser.setVisibility(View.GONE);
            holder.tvAiMessage.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class AiViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutUser, layoutAi;
        TextView tvUserMessage, tvAiMessage;

        public AiViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutUser = itemView.findViewById(R.id.layoutUser);
            layoutAi = itemView.findViewById(R.id.layoutAi);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
            tvAiMessage = itemView.findViewById(R.id.tvAiMessage);
        }
    }
}