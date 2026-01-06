const mongoose = require('mongoose');

const DocumentSchema = new mongoose.Schema({
    title: { type: String, required: true }, // Tên tài liệu người dùng nhập
    fileName: { type: String, required: true }, // Tên file gốc (ví dụ: btap.pdf)
    fileUrl: { type: String, required: true }, // Đường dẫn để tải file
    uploader: { type: String, default: "Sinh viên" }, // Lấy từ thông tin người dùng
    createdAt: { type: Date, default: Date.now } // Tự động lưu ngày upload
});

module.exports = mongoose.model('Document', DocumentSchema);