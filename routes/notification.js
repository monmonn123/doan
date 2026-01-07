const express = require('express');
const router = express.Router();
const { getNotifications, markAsRead, getUserNotifications } = require('../controller/notificationController');
const auth = require('../middleware/auth');

router.use(auth);

router.get('/', getNotifications); // Route này có thể để lấy tất cả thông báo cho admin
router.get('/:userId', getUserNotifications); // Route mới cho user
router.put('/read/:id', markAsRead);

module.exports = router;