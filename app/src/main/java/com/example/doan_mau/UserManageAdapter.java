package com.example.doan_mau;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class UserManageAdapter extends RecyclerView.Adapter<UserManageAdapter.UserViewHolder> {

    private List<Map<String, Object>> users;
    private OnUserActionClickListener listener;

    public interface OnUserActionClickListener {
        void onRoleChange(String userId, String newRole);
    }

    public UserManageAdapter(List<Map<String, Object>> users, OnUserActionClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_manage, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Map<String, Object> user = users.get(position);
        String id = (String) user.get("_id");
        String name = (String) user.get("hoTen");
        String mssv = (String) user.get("mssv");
        String role = (String) user.get("role");

        holder.tvName.setText(name);
        holder.tvMssv.setText("MSSV: " + mssv);
        holder.tvRole.setText("Quyền hiện tại: " + role);

        if ("manager".equals(role)) {
            holder.btnAction.setText("Hạ xuống User");
            holder.btnAction.setBackgroundColor(0xFFEF4444); // Màu đỏ
        } else {
            holder.btnAction.setText("Lên Manager");
            holder.btnAction.setBackgroundColor(0xFF3B82F6); // Màu xanh
        }

        holder.btnAction.setOnClickListener(v -> {
            String newRole = "manager".equals(role) ? "user" : "manager";
            listener.onRoleChange(id, newRole);
        });
    }

    @Override
    public int getItemCount() { return users.size(); }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMssv, tvRole;
        Button btnAction;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvManageUserName);
            tvMssv = itemView.findViewById(R.id.tvManageUserMssv);
            tvRole = itemView.findViewById(R.id.tvUserRole);
            btnAction = itemView.findViewById(R.id.btnChangeRole);
        }
    }
}