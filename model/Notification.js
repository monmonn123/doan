const mongoose = require('mongoose');

const notificationSchema = new mongoose.Schema({
    receiverId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    senderId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' }, // Người tương tác
    type: { 
        type: String, 
        enum: ['approve', 'interaction', 'report', 'system'],
        required: true 
    },
    action: { type: String }, // 'like', 'dislike', 'comment', 'report_post', etc.
    content: { type: String, required: true },
    relatedId: { type: String }, // ID của bài viết hoặc comment liên quan
    isRead: { type: Boolean, default: false }
}, { timestamps: true });

module.exports = mongoose.model('Notification', notificationSchema);