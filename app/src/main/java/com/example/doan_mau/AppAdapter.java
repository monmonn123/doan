package com.example.doan_mau;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private Context mContext;
    private List<ApplicationInfo> mAppList;
    private Set<String> mSelectedApps; // Đổi tên để rõ nghĩa hơn
    private boolean mIsBlockingActive;

    public AppAdapter(Context context, List<ApplicationInfo> appList, Set<String> selectedApps) {
        this.mContext = context;
        this.mAppList = appList;
        this.mSelectedApps = selectedApps != null ? selectedApps : new HashSet<>();
        
        // Kiểm tra xem chế độ chặn có đang hoạt động không
        SharedPreferences prefs = context.getSharedPreferences("AppBlocker", Context.MODE_PRIVATE);
        long blockUntil = prefs.getLong("block_until", 0);
        this.mIsBlockingActive = System.currentTimeMillis() < blockUntil;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        ApplicationInfo appInfo = mAppList.get(position);
        PackageManager pm = mContext.getPackageManager();

        holder.appName.setText(appInfo.loadLabel(pm));
        holder.appIcon.setImageDrawable(appInfo.loadIcon(pm));

        holder.appCheckbox.setChecked(mSelectedApps.contains(appInfo.packageName));

        holder.appCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mSelectedApps.add(appInfo.packageName);
            } else {
                mSelectedApps.remove(appInfo.packageName);
            }
        });

        // Làm mờ ứng dụng nếu đang trong thời gian chặn
        if (mIsBlockingActive && mSelectedApps.contains(appInfo.packageName)) {
            holder.itemView.setAlpha(0.5f);
            holder.appCheckbox.setEnabled(false);
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.appCheckbox.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return mAppList != null ? mAppList.size() : 0;
    }

    public Set<String> getSelectedApps() {
        return mSelectedApps;
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        CheckBox appCheckbox;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            appCheckbox = itemView.findViewById(R.id.appCheckbox);
        }
    }
}
