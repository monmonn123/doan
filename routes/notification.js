const express = require('express');
const router = express.Router();
const { getNotifications, markAsRead } = require('../controller/notificationController');
const auth = require('../middleware/auth');

router.use(auth);

router.get('/', getNotifications);
router.put('/read/:id', markAsRead);

module.exports = router;