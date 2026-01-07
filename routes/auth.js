// routes/auth.js
const express = require('express');
const router = express.Router();

// Đảm bảo đường dẫn tới file controller chính xác
const authController = require('../controller/authController');

// SỬA TẠI ĐÂY: Thêm authController. vào trước register và login
router.post('/register', authController.register);
router.post('/login', authController.login);

router.post('/forgot-password/request', authController.requestForgotPasswordOTP);
router.post('/forgot-password/reset', authController.resetPassword);

module.exports = router;