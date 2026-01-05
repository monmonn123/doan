package com.example.doan_mau;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<StudentData.Category> mList;
    private Context mContext;
    private MediaPlayer mediaPlayer;

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

        if (holder.imgIcon.getBackground() != null) {
            holder.imgIcon.getBackground().mutate().setColorFilter(category.color, PorterDuff.Mode.SRC_IN);
        }
        holder.imgIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        if (category.isExpanded) {
            holder.layoutFeatures.setVisibility(View.VISIBLE);
            holder.imgArrow.setRotation(180);
            holder.layoutFeatures.removeAllViews();

            for (StudentData.Feature feature : category.featureList) {
                addFeatureItem(holder.layoutFeatures, feature, category.id);
            }
        } else {
            holder.layoutFeatures.setVisibility(View.GONE);
            holder.imgArrow.setRotation(0);
        }

        holder.layoutHeader.setOnClickListener(v -> {
            category.isExpanded = !category.isExpanded;
            notifyItemChanged(position);
        });
    }

    private void addFeatureItem(LinearLayout container, StudentData.Feature feature, String categoryId) {
        View featureView = LayoutInflater.from(mContext).inflate(R.layout.item_feature, container, false);

        TextView tvName = featureView.findViewById(R.id.tvFeatureName);
        TextView tvDetail = featureView.findViewById(R.id.tvFeatureDetail);
        ImageView imgIcon = featureView.findViewById(R.id.imgFeatureIcon);

        tvName.setText(feature.name);
        tvDetail.setText(feature.detail);
        imgIcon.setImageResource(feature.iconResId);

        featureView.setOnClickListener(v -> {
            if ("focus".equals(categoryId)) {
                handleFocusFeatures(feature.name);
            } else {
                Toast.makeText(mContext, "Mở chức năng: " + feature.name, Toast.LENGTH_SHORT).show();
            }
        });

        container.addView(featureView);
    }

    private void handleFocusFeatures(String featureName) {
        switch (featureName) {
            case "Chặn App gây nghiện":
                mContext.startActivity(new Intent(mContext, AppBlockerActivity.class));
                break;
            case "Đồng hồ Focus":
                mContext.startActivity(new Intent(mContext, PomodoroActivity.class));
                break;
            case "Thống kê tập trung":
                Toast.makeText(mContext, "Mở biểu đồ thống kê thời gian đã tập trung!", Toast.LENGTH_SHORT).show();
                break;
            case "Nhạc White Noise":
                playOrStopMusic();
                break;
            default:
                Toast.makeText(mContext, "Mở chức năng: " + featureName, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void playOrStopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Toast.makeText(mContext, "Đã tắt nhạc...", Toast.LENGTH_SHORT).show();
        } else {
            // Bạn cần tự tạo thư mục res/raw và thêm file nhạc vào
            // mediaPlayer = MediaPlayer.create(mContext, R.raw.rain_sound);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                Toast.makeText(mContext, "Đang phát nhạc mưa...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Chưa có file nhạc. Hãy thêm file âm thanh vào res/raw", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutHeader, layoutFeatures;
        TextView tvTitle, tvDesc;
        ImageView imgIcon, imgArrow;
        CardView cardView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutHeader = itemView.findViewById(R.id.layoutHeader);
            layoutFeatures = itemView.findViewById(R.id.layoutFeatures);
            tvTitle = itemView.findViewById(R.id.tvCategoryTitle);
            tvDesc = itemView.findViewById(R.id.tvCategoryDesc);
            imgIcon = itemView.findViewById(R.id.imgCategoryIcon);
            imgArrow = itemView.findViewById(R.id.imgArrow);
            cardView = itemView.findViewById(R.id.cardContainer);
        }
    }
}
