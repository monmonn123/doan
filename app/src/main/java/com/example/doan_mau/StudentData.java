package com.example.doan_mau;

import android.graphics.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudentData {

    public static class Feature {
        public String name;
        public String detail;
        public int iconResId;

        public Feature(String name, String detail, int iconResId) {
            this.name = name;
            this.detail = detail;
            this.iconResId = iconResId;
        }
    }

    public static class Category {
        public String id;
        public String title;
        public String description;
        public int color;
        public int mainIconResId;
        public List<Feature> featureList;
        public boolean isExpanded = false;

        public Category(String id, String title, String description, int color, int mainIconResId, List<Feature> featureList) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.color = color;
            this.mainIconResId = mainIconResId;
            this.featureList = featureList;
        }
    }

    public static List<Category> getMockData() {
        List<Category> categories = new ArrayList<>();

        List<Feature> progressFeatures = Arrays.asList(
                new Feature("Thời khóa biểu", "Quản lý lịch học của bạn", android.R.drawable.ic_menu_today),
                new Feature("Công việc cần làm", "Danh sách To-Do có nhắc nhở", android.R.drawable.ic_menu_edit)
        );
        categories.add(new Category("progress", "Tiến độ học tập", "Theo dõi và quản lý học tập",
                Color.parseColor("#3498DB"), android.R.drawable.ic_menu_my_calendar, progressFeatures));

        List<Feature> docFeatures = Arrays.asList(
                new Feature("Upload tài liệu", "Tải tài liệu lên hệ thống", android.R.drawable.ic_menu_upload),
                new Feature("Danh sách tài liệu", "Xem các tài liệu đã chia sẻ", android.R.drawable.ic_menu_recent_history)
        );
        categories.add(new Category("document", "Kho Tài liệu", "Chia sẻ tài liệu học tập",
                android.graphics.Color.parseColor("#2ECC71"), android.R.drawable.ic_menu_directions, docFeatures));

        List<Feature> focusFeatures = new ArrayList<>();
        focusFeatures.add(new Feature("Chặn App", "Tạm khóa MXH khi học.", android.R.drawable.ic_delete));
        focusFeatures.add(new Feature("Đồng hồ Focus", "Đếm ngược thời gian học.", android.R.drawable.ic_lock_idle_alarm));
        focusFeatures.add(new Feature("Nhạc thư giãn", "Nhạc không lời tập trung.", android.R.drawable.ic_media_play));
        categories.add(new Category("focus", "Góc Tập trung", "Công cụ tối ưu hiệu suất", 0xFFF97316, android.R.drawable.ic_lock_power_off, focusFeatures));

        return categories;
    }

}
