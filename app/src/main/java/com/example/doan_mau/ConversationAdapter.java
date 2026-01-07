package com.example.doan_mau;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConvViewHolder> {

    private List<Map<String, Object>> conversations;
    private OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(String userId, String userName);
    }

    public ConversationAdapter(List<Map<String, Object>> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConvViewHolder holder, int position) {
        Map<String, Object> conv = conversations.get(position);
        
        String name = (String) conv.get("hoTen");
        String lastMsg = (String) conv.get("lastMessage");

        holder.tvName.setText(name);
        holder.tvLastMsg.setText(lastMsg);

        holder.itemView.setOnClickListener(v -> {
            listener.onConversationClick((String) conv.get("_id"), name);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class ConvViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMsg;

        public ConvViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvConvUserName);
            tvLastMsg = itemView.findViewById(R.id.tvLastMessage);
        }
    }
}