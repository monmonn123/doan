package com.example.doan_mau;

import java.util.List;

public class ReportModel {
    public String _id;
    public String reportedUserId;
    public String reason;
    public String description;
    public String status;
    public String createdAt;
    public UserObj userId;

    public static class UserObj {
        public String _id;
        public String hoTen;
        public String mssv;
        public String avatar;
    }

    public String getFormattedDate() {
        if (createdAt != null && !createdAt.isEmpty()) {
            try {
                // Format: 2024-01-15T10:30:00.000Z -> 15/01/2024
                String date = createdAt.substring(0, 10);
                String[] parts = date.split("-");
                if (parts.length == 3) {
                    return parts[2] + "/" + parts[1] + "/" + parts[0];
                }
            } catch (Exception e) {
                return createdAt;
            }
        }
        return "";
    }

    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case "pending": return "Đang chờ";
            case "reviewed": return "Đã xem xét";
            case "resolved": return "Đã xử lý";
            case "rejected": return "Từ chối";
            default: return status;
        }
    }

    public String getReasonText() {
        if (reason == null) return "";
        switch (reason) {
            case "spam": return "Spam / Quảng cáo";
            case "harassment": return "Quấy rối / Xúc phạm";
            case "inappropriate_content": return "Nội dung không phù hợp";
            case "fake_account": return "Tài khoản giả mạo";
            case "other": return "Khác";
            default: return reason;
        }
    }
}

