const Notification = require('../model/Notification');

const getNotifications = async (req, res) => {
    try {
        const userId = req.user.id;
        const notifications = await Notification.find({ receiverId: userId })
            .populate('senderId', 'hoTen mssv')
            .sort({ createdAt: -1 });
        res.json({ success: true, notifications });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

const markAsRead = async (req, res) => {
    try {
        const { id } = req.params;
        await Notification.findByIdAndUpdate(id, { isRead: true });
        res.json({ success: true, message: 'Đã đánh dấu đã đọc' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

const createNotification = async (receiverId, senderId, type, action, content, relatedId) => {
    try {
        if (receiverId.toString() === senderId?.toString()) return; // Không thông báo cho chính mình

        const newNoti = new Notification({
            receiverId,
            senderId,
            type,
            action,
            content,
            relatedId
        });
        await newNoti.save();
    } catch (error) {
        console.error('Lỗi tạo thông báo:', error);
    }
};

module.exports = { getNotifications, markAsRead, createNotification };