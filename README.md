# 🎓 MyUTE - Ứng dụng Hỗ trợ Học tập & Kết nối Sinh viên UTE

**MyUTE** là dự án ứng dụng Android được phát triển cho môn học **Lập trình trên thiết bị di động**. Ứng dụng là một hệ sinh thái hỗ trợ học tập dành riêng cho sinh viên trường **Đại học Sư phạm Kỹ thuật TP.HCM (UTE)**, giúp quản lý lộ trình học tập, tối ưu hiệu suất và kết nối cộng đồng sinh viên.

---

## ✨ Chức năng chính

### 🔐 1. Luồng Người dùng & Xác thực
* **Đăng nhập (Login):** Người dùng thực hiện đăng nhập bằng **Mã số sinh viên (MSSV)** và mật khẩu cá nhân. Đây là màn hình khởi đầu của ứng dụng.
* **Đăng ký (Register):** Hỗ trợ sinh viên tạo tài khoản mới thông qua **MSSV**. Hệ thống tích hợp xác thực qua **mã OTP (6 số)** để đảm bảo chính chủ.
* **Điều hướng:** Sau khi đăng nhập thành công hoặc kích hoạt OTP, người dùng sẽ được chuyển trực tiếp đến **Trang chủ (Main)** để trải nghiệm dịch vụ.

### 📅 2. Quản lý Học tập & Thông báo
* **Tiến độ học tập:** * Cho phép người dùng tạo **ghi chú (note)** chi tiết về thời khóa biểu, lịch thi và lộ trình học tập cá nhân.
* **Thông báo (Notification):** * Tự động gửi **nhắc nhở trên điện thoại** khi đến giờ học hoặc các mốc thời gian quan trọng đã được lưu trong lịch trình.
* **Kho Tài liệu:** * Nơi lưu trữ và chia sẻ tài liệu học tập. Hỗ trợ **upload** và tra cứu bài giảng, đề thi theo từng môn học.
* **Góc Tập trung (Focus Mode):** * Bộ công cụ giúp sinh viên học tập chuyên sâu bao gồm: **Chặn ứng dụng** gây xao nhãng, **đồng hồ Pomodoro** và **nhạc thư giãn** (Lo-fi/White noise).

### 🤖 3. Hệ thống Hỗ trợ AI & Cộng đồng
* **Trợ lý AI:** * Tích hợp **ChatBot học tập** sử dụng trí tuệ nhân tạo để giải đáp nhanh kiến thức chuyên môn và hỗ trợ phương pháp giải bài tập.
* **Blog Trao đổi:** * **Blog học tập:** Nơi chia sẻ kinh nghiệm, bí kíp ôn thi từ các sinh viên.
    * **Đăng bài hỏi đáp:** Diễn đàn tương tác, nơi sinh viên có thể đặt câu hỏi và hỗ trợ giải đáp thắc mắc lẫn nhau.

---

## 🛠 Công nghệ sử dụng

* **Ngôn ngữ lập trình:** **Java**
* **Nền tảng phát triển:** **Android Studio**
* **Xử lý thông báo:** **AlarmManager** & **NotificationChannel** (Đảm bảo thông báo chính xác theo thời gian thực).
* **Lưu trữ dữ liệu:** **SQLite / Room Database** (Dữ liệu nội bộ) & **Firebase** (Dữ liệu đám mây/Xác thực).
* **Kết nối hệ thống:** **RESTful API** cho xác thực, quản lý lịch trình, kho tài liệu và tích hợp AI.

---

## 👥 Thành viên thực hiện

Dự án được phát triển bởi nhóm sinh viên **UTE**:

* **Lê Trương Hồng Phước** (Nhóm trưởng)
* **Lê Thị Thùy Dung**
* **Phạm Tiến Đạt**
* **Nguyễn Quốc Khang**

---
*Dự án mang tính chất học thuật phục vụ cho cộng đồng sinh viên UTE.*
