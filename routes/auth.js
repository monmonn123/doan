// routes/auth.js
const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const { generateToken, verifyToken, JWT_SECRET } = require('../config/auth');

// Đảm bảo đường dẫn tới file controller chính xác
const authController = require('../controller/authController');

// SỬA TẠI ĐÂY: Thêm authController. vào trước register và login
router.post('/register', authController.register);
router.post('/login', authController.login);

router.post('/forgot-password/request', authController.requestForgotPasswordOTP);
router.post('/forgot-password/reset', authController.resetPassword);

// DEBUG ENDPOINT: Test token generation and verification
router.post('/test-token', async(req, res) => {
    try {
        const { userId, token } = req.body;

        if (userId) {
            // Test token generation
            const generatedToken = generateToken(userId);

            // Decode without verification to show payload
            const decoded = jwt.decode(generatedToken);

            res.json({
                success: true,
                message: 'Token generated successfully',
                token: generatedToken,
                payload: decoded,
                jwtSecret: JWT_SECRET.substring(0, 5) + '...' // Show first 5 chars only
            });
        } else if (token) {
            // Test token verification
            try {
                const decoded = verifyToken(token);
                res.json({
                    success: true,
                    message: 'Token verified successfully',
                    payload: decoded
                });
            } catch (error) {
                res.json({
                    success: false,
                    message: 'Token verification failed',
                    error: error.message,
                    errorName: error.message.split(':')[0]
                });
            }
        } else {
            res.status(400).json({ success: false, message: 'Provide userId or token' });
        }
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

module.exports = router;