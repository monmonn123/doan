const express = require('express');
const router = express.Router();
const Message = require('../model/Message');
const { searchForAdmin, searchUserByMssv } = require('../controller/messageController');
const { getConversationList, getUserChatHistory, sendUserMessage } = require('../controller/chatController');


// --- TÌM KIẾM & LỊCH SỬ CHAT ---
router.get('/search/admin', searchForAdmin);
router.get('/search/:mssv', searchUserByMssv);
router.get('/conversations/:userId', getConversationList);
router.get('/history/:senderId/:receiverId', getUserChatHistory);

// --- GỬI TIN NHẮN ---
router.post('/send', sendUserMessage);


module.exports = router;