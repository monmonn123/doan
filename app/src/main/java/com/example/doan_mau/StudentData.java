package com.example.doan_mau;

import java.util.ArrayList;
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

        // 1. Lịch trình & Điểm danh (Dùng icon hệ thống android.R.drawable...)
        List<Feature> scheduleFeatures = new ArrayList<>();
        scheduleFeatures.add(new Feature("Điểm danh bằng QR", "Quét mã QR để điểm danh vào lớp ngay lập tức.", android.R.drawable.ic_menu_camera));
        scheduleFeatures.add(new Feature("Lịch học & Deadline", "Xem lịch học, hạn nộp bài tập.", android.R.drawable.ic_menu_my_calendar));
        scheduleFeatures.add(new Feature("Lịch sử điểm danh", "Xem lại các lần điểm danh.", android.R.drawable.ic_menu_recent_history));

        categories.add(new Category("schedule", "Lịch trình & Điểm danh", "Quản lý thời gian và điểm danh",
                0xFF3B82F6, android.R.drawable.ic_menu_agenda, scheduleFeatures));

        // 2. Tài liệu (Dùng icon hệ thống)
        List<Feature> docsFeatures = new ArrayList<>();
        docsFeatures.add(new Feature("Upload tài liệu", "Tải lên giáo trình PDF.", android.R.drawable.ic_menu_upload));
        docsFeatures.add(new Feature("Tìm kiếm môn học", "Tìm tài liệu theo mã môn.", android.R.drawable.ic_menu_search));
        docsFeatures.add(new Feature("Cộng đồng", "Chia sẻ tài liệu với bạn bè.", android.R.drawable.star_on));

        categories.add(new Category("documents", "Kho Tài liệu", "Chia sẻ và tìm kiếm tài liệu",
                0xFF10B981, android.R.drawable.ic_menu_sort_by_size, docsFeatures));

        // 3. Trợ lý AI (Dùng icon hệ thống)
        List<Feature> aiFeatures = new ArrayList<>();
        aiFeatures.add(new Feature("ChatBot học tập", "Hỏi đáp kiến thức với AI.", android.R.drawable.ic_menu_help));
        aiFeatures.add(new Feature("Gợi ý bài tập", "Đề xuất bài luyện tập.", android.R.drawable.ic_menu_compass));

        categories.add(new Category("ai", "Trợ lý AI", "Hỗ trợ giải bài tập",
                0xFFA855F7, android.R.drawable.ic_menu_view, aiFeatures));

        // 4. Focus (Dùng icon hệ thống)
        List<Feature> focusFeatures = new ArrayList<>();
        focusFeatures.add(new Feature("Chặn App", "Tạm khóa MXH khi học.", android.R.drawable.ic_delete));
        focusFeatures.add(new Feature("Đồng hồ Focus", "Đếm ngược thời gian học.", android.R.drawable.ic_lock_idle_alarm));
        focusFeatures.add(new Feature("Nhạc thư giãn", "Nhạc không lời tập trung.", android.R.drawable.ic_media_play));

        categories.add(new Category("focus", "Góc Tập trung", "Công cụ tối ưu hiệu suất",
                0xFFF97316, android.R.drawable.ic_lock_power_off, focusFeatures));

        return categories;
    }
}