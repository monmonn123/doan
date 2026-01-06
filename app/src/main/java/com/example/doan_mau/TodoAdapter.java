package com.example.doan_mau;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<String> taskList;
    private OnTodoClickListener listener;

    public interface OnTodoClickListener {
        void onTodoClick(int position); // Dùng để hiện menu Sửa/Xóa
    }

    public TodoAdapter(List<String> taskList, OnTodoClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        String data = taskList.get(position);
        String[] split = data.split("\\|");
        String name = split[0];
        String time = split[1];

        holder.tvName.setText(name);
        holder.tvTime.setText("🕒 " + time);

        // Phân biệt màu sắc chuyên nghiệp
        if (name.contains("Lịch học")) {
            holder.tvName.setTextColor(Color.parseColor("#27AE60")); // Xanh lá
        } else {
            holder.tvName.setTextColor(Color.parseColor("#3498DB")); // Xanh dương
        }

        holder.itemView.setOnClickListener(v -> listener.onTodoClick(position));
    }

    @Override
    public int getItemCount() { return taskList.size(); }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime;
        CheckBox cbDone;
        public TodoViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvTodoName);
            tvTime = v.findViewById(R.id.tvTodoTime);
            cbDone = v.findViewById(R.id.cbDone);
        }
    }
}