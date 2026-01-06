package com.example.doan_mau;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotiViewHolder> {

    private List<Map<String, String>> notifications;

    public NotificationAdapter(List<Map<String, String>> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        Map<String, String> noti = notifications.get(position);
        holder.tvTitle.setText(noti.get("title"));
        holder.tvContent.setText(noti.get("content"));
        holder.tvTime.setText(noti.get("time"));
        
        if ("message".equals(noti.get("type"))) {
            holder.imgIcon.setImageResource(android.R.drawable.ic_dialog_email);
        } else {
            holder.imgIcon.setImageResource(android.R.drawable.ic_dialog_info);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime;
        ImageView imgIcon;

        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotiTitle);
            tvContent = itemView.findViewById(R.id.tvNotiContent);
            tvTime = itemView.findViewById(R.id.tvNotiTime);
            imgIcon = itemView.findViewById(R.id.imgNotiIcon);
        }
    }
}