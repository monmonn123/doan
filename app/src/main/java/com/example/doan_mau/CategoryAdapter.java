package com.example.doan_mau;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<StudentData.Category> mList;
    private Context mContext;

    public CategoryAdapter(Context context, List<StudentData.Category> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        StudentData.Category category = mList.get(position);
        if (category == null) return;

        holder.tvTitle.setText(category.title);
        holder.tvDesc.setText(category.description);
        holder.imgIcon.setImageResource(category.mainIconResId);

        if (category.isExpanded) {
            holder.layoutFeatures.setVisibility(View.VISIBLE);
            holder.layoutFeatures.removeAllViews();
            for (StudentData.Feature feature : category.featureList) {
                addFeatureItem(holder.layoutFeatures, feature, category.id);
            }
        } else {
            holder.layoutFeatures.setVisibility(View.GONE);
        }

        holder.layoutHeader.setOnClickListener(v -> {
            if ("progress".equals(category.id)) {
                // Bấm vào tiêu đề là mở thẳng trang Tiến độ luôn
                mContext.startActivity(new Intent(mContext, ProgressActivity.class));
            } else {
                // Các cái khác (Nhạc, Tài liệu...) thì vẫn cho xổ xuống như cũ
                category.isExpanded = !category.isExpanded;
                notifyItemChanged(position);
            }
        });
    }

    private void addFeatureItem(LinearLayout container, StudentData.Feature feature, String categoryId) {
        View featureView = LayoutInflater.from(mContext).inflate(R.layout.item_feature, container, false);
        TextView tvName = featureView.findViewById(R.id.tvFeatureName);
        tvName.setText(feature.name);

        featureView.setOnClickListener(v -> {
            if ("focus".equals(categoryId)) {
                handleFocusFeatures(feature.name);
            } else if ("document".equals(categoryId)) {
                handleDocumentFeatures(feature.name);
            } else if ("progress".equals(categoryId)) {
                // KẾT NỐI CHỨC NĂNG MỚI: Tiến độ học tập
                mContext.startActivity(new Intent(mContext, ProgressActivity.class));
            } else {
                Toast.makeText(mContext, "Chức năng: " + feature.name, Toast.LENGTH_SHORT).show();
            }
        });
        container.addView(featureView);
    }

    private void handleFocusFeatures(String featureName) {
        switch (featureName) {
            case "Chặn App":
                mContext.startActivity(new Intent(mContext, AppBlockerActivity.class));
                break;
            case "Đồng hồ Focus":
                mContext.startActivity(new Intent(mContext, PomodoroActivity.class));
                break;
            case "Nhạc thư giãn":
                mContext.startActivity(new Intent(mContext, MusicPlayerActivity.class));
                break;
        }
    }

    private void handleDocumentFeatures(String featureName) {
        if ("Upload tài liệu".equals(featureName)) {
            mContext.startActivity(new Intent(mContext, UploadActivity.class));
        } else if ("Danh sách tài liệu".equals(featureName)) {
            mContext.startActivity(new Intent(mContext, DocumentListActivity.class));
        }
    }

    @Override
    public int getItemCount() { return mList != null ? mList.size() : 0; }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutHeader, layoutFeatures;
        TextView tvTitle, tvDesc;
        ImageView imgIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutHeader = itemView.findViewById(R.id.layoutHeader);
            layoutFeatures = itemView.findViewById(R.id.layoutFeatures);
            tvTitle = itemView.findViewById(R.id.tvCategoryTitle);
            tvDesc = itemView.findViewById(R.id.tvCategoryDesc);
            imgIcon = itemView.findViewById(R.id.imgCategoryIcon);
        }
    }
}