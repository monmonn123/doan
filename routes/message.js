const express = require('express');
const router = express.Router();
const { searchUserByMssv, getMessages, sendMessage, getConversationList } = require('../controller/messageController');
const auth = require('../middleware/auth');

router.use(auth);

router.get('/search/:mssv', searchUserByMssv);
router.get('/history/:senderId/:receiverId', getMessages);
router.post('/send', sendMessage);
router.get('/conversations/:userId', getConversationList);

module.exports = router;