const express = require('express');
const router = express.Router();
const Message = require('../model/Message');

// --- ROUTE CHO NGƯỜI DÙNG (USER) ---

// [POST] /api/messages/ -> Gửi tin nhắn mới (Người dùng gửi)
router.post('/', async (req, res) => {
    try {
        const { userId, text } = req.body;
        // Kiểm tra dữ liệu đầu vào
        if (!userId || !text) {
            return res.status(400).json({ message: "UserId và nội dung tin nhắn là bắt buộc." });
        }
        const newMessage = new Message({
            userId,
            text,
            sender: 'user' // Đánh dấu người gửi là user
        });
        await newMessage.save();
        res.status(201).json(newMessage);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// [GET] /api/messages/:userId -> Lấy lịch sử chat của MỘT người dùng
router.get('/:userId', async (req, res) => {
    try {
        const messages = await Message.find({ userId: req.params.userId })
            .populate('userId', 'hoTen mssv avatar') // Lấy thêm thông tin người dùng
            .sort({ createdAt: 'asc' }); // Sắp xếp từ cũ đến mới
        res.json(messages);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});


// --- ROUTES CHO QUẢN TRỊ VIÊN (ADMIN) ---

// [GET] /api/messages/admin/conversations -> Lấy danh sách tất cả các cuộc trò chuyện
router.get('/admin/conversations', async (req, res) => {
    try {
        // Sử dụng pipeline của MongoDB để nhóm tin nhắn theo userId
        // và lấy ra tin nhắn cuối cùng của mỗi cuộc trò chuyện
        const conversations = await Message.aggregate([
            // Sắp xếp tất cả tin nhắn theo thời gian giảm dần
            { $sort: { createdAt: -1 } },
            // Nhóm theo userId và lấy thông tin của tin nhắn đầu tiên (là tin nhắn mới nhất)
            {
                $group: {
                    _id: "$userId", // ID của group chính là userId
                    lastMessage: { $first: "$text" },
                    lastMessageTime: { $first: "$createdAt" },
                    sender: { $first: "$sender" }
                }
            },
            // Join với bảng User để lấy thông tin người chat (hoTen, avatar)
            {
                $lookup: {
                    from: "users", // Tên collection của User model
                    localField: "_id",
                    foreignField: "_id",
                    as: "userDetails"
                }
            },
            // "Mở" mảng userDetails ra
            { $unwind: "$userDetails" },
             // Sắp xếp các cuộc trò chuyện, cuộc nào mới nhất đưa lên đầu
            { $sort: { lastMessageTime: -1 } },
            // Chọn các trường cần hiển thị
            {
                $project: {
                    userId: "$_id",
                    userName: "$userDetails.hoTen",
                    userAvatar: "$userDetails.avatar",
                    lastMessage: "$lastMessage",
                    lastMessageTime: "$lastMessageTime",
                    sender: "$sender"
                }
            }
        ]);
        res.json(conversations);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// [POST] /api/messages/admin/reply -> Admin trả lời tin nhắn của người dùng
router.post('/admin/reply', async (req, res) => {
    try {
        // Admin cần gửi userId của người mà họ muốn trả lời
        const { userId, text } = req.body;
        if (!userId || !text) {
            return res.status(400).json({ message: "UserId và nội dung tin nhắn là bắt buộc." });
        }
        const newReply = new Message({
            userId,
            text,
            sender: 'admin' // Đánh dấu người gửi là admin
        });
        await newReply.save();
        res.status(201).json(newReply);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

module.exports = router;
