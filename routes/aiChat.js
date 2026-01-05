const express = require('express');
const {
    sendMessage,
    getChatHistory,
    getUserConversations,
    deleteConversation,
    deleteMessage,
    getChatStats
} = require('../controller/aiChatController');
const auth = require('../middleware/auth');

const router = express.Router();

// Tất cả routes đều cần authentication
router.use(auth);

// Gửi tin nhắn và nhận phản hồi từ AI
router.post('/send', sendMessage);

// Lấy lịch sử chat của một cuộc trò chuyện
router.get('/history/:conversationId', getChatHistory);

// Lấy danh sách cuộc trò chuyện của user
router.get('/conversations', getUserConversations);

// Xóa cuộc trò chuyện
router.delete('/conversation/:conversationId', deleteConversation);

// Xóa tin nhắn cụ thể
router.delete('/message/:chatId', deleteMessage);

// Thống kê chat của user
router.get('/stats', getChatStats);

module.exports = router;