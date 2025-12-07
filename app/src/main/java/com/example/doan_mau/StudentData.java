package com.example.doan_mau; // Đổi theo package của bạn

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

        // 1. Lịch trình & Điểm danh
        List<Feature> scheduleFeatures = new ArrayList<>();
        scheduleFeatures.add(new Feature("Điểm danh bằng QR", "Quét mã QR để điểm danh vào lớp ngay lập tức.", R.drawable.ic_qr_code));
        scheduleFeatures.add(new Feature("Lịch học & Deadline", "Xem lịch học, hạn nộp bài tập và nhận nhắc nhở.", R.drawable.ic_access_time));
        scheduleFeatures.add(new Feature("Đồng bộ Google Calendar", "Tự động đồng bộ lịch cá nhân với lịch trường.", R.drawable.ic_sync));
        scheduleFeatures.add(new Feature("Widget lịch ngày", "Tiện ích màn hình chính hiển thị lịch trình hôm nay.", R.drawable.ic_widgets));
        scheduleFeatures.add(new Feature("Lịch sử điểm danh", "Xem lại lịch sử các lần điểm danh đã lưu.", R.drawable.ic_history));

        categories.add(new Category("schedule", "Lịch trình & Điểm danh", "Quản lý thời gian và điểm danh lớp học", 0xFF3B82F6, R.drawable.ic_calendar_today, scheduleFeatures));

        // 2. Kho Tài liệu & Cộng đồng
        List<Feature> docsFeatures = new ArrayList<>();
        docsFeatures.add(new Feature("Upload tài liệu PDF", "Tải lên giáo trình, bài giảng PDF.", R.drawable.ic_cloud_upload));
        docsFeatures.add(new Feature("Tìm kiếm theo môn", "Bộ lọc tài liệu thông minh theo mã môn học.", R.drawable.ic_search));
        docsFeatures.add(new Feature("Bảng xếp hạng tài liệu", "Top tài liệu được tải nhiều nhất tháng.", R.drawable.ic_bar_chart));
        docsFeatures.add(new Feature("Cộng đồng (Like/Follow)", "Tương tác, bình luận và theo dõi người chia sẻ.", R.drawable.ic_favorite_border));

        categories.add(new Category("documents", "Kho Tài liệu & Cộng đồng", "Chia sẻ và tìm kiếm tài liệu học tập", 0xFF10B981, R.drawable.ic_description, docsFeatures));

        // 3. Trợ lý Học tập AI
        List<Feature> aiFeatures = new ArrayList<>();
        aiFeatures.add(new Feature("ChatBot học tập", "Hỏi đáp kiến thức 24/7 với AI.", R.drawable.ic_chat_bubble_outline));
        aiFeatures.add(new Feature("Scan bài tập", "Chụp ảnh bài tập, AI phân tích và giải thích.", R.drawable.ic_camera_alt));
        aiFeatures.add(new Feature("Hỗ trợ kế hoạch học", "Lập lộ trình ôn thi cá nhân hóa.", R.drawable.ic_edit_calendar));
        aiFeatures.add(new Feature("Gợi ý bài tập", "Đề xuất bài luyện tập dựa trên điểm yếu.", R.drawable.ic_lightbulb));

        categories.add(new Category("ai", "Trợ lý Học tập AI", "Hỗ trợ giải bài và lên kế hoạch", 0xFFA855F7, R.drawable.ic_psychology, aiFeatures));

        // 4. Góc Tập trung (Focus)
        List<Feature> focusFeatures = new ArrayList<>();
        focusFeatures.add(new Feature("Chặn App gây nghiện", "Tạm khóa TikTok, Facebook trong giờ học.", R.drawable.ic_app_blocking));
        focusFeatures.add(new Feature("Đồng hồ Focus", "Pomodoro timer và đo thời gian thực học.", R.drawable.ic_hourglass_top));
        focusFeatures.add(new Feature("Thống kê tập trung", "Biểu đồ phân tích mức độ tập trung tuần.", R.drawable.ic_analytics));
        focusFeatures.add(new Feature("Nhạc White Noise", "Phát nhạc Lofi/Mưa rơi giúp tập trung.", R.drawable.ic_music_note));

        categories.add(new Category("focus", "Góc Tập trung (Focus)", "Công cụ tối ưu hiệu suất học", 0xFFF97316, R.drawable.ic_timer, focusFeatures));

        return categories;
    }
}
