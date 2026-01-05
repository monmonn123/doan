const jwt = require('jsonwebtoken');
const { verifyToken } = require('../config/auth');
const User = require('../model/User');

const auth = async(req, res, next) => {
    try {
        // Get token from header
        const authHeader = req.header('Authorization');

        if (!authHeader) {
            return res.status(401).json({
                success: false,
                message: 'Không có token, truy cập bị từ chối'
            });
        }

        // Check if token starts with 'Bearer '
        const token = authHeader.startsWith('Bearer ') ?
            authHeader.slice(7, authHeader.length) :
            authHeader;

        if (!token) {
            return res.status(401).json({
                success: false,
                message: 'Token không hợp lệ'
            });
        }

        // Verify token
        const decoded = verifyToken(token);

        // Get user from token
        const user = await User.findById(decoded.userId).select('-password');

        if (!user) {
            return res.status(401).json({
                success: false,
                message: 'Token không hợp lệ'
            });
        }

        if (!user.isActive) {
            return res.status(401).json({
                success: false,
                message: 'Tài khoản đã bị vô hiệu hóa'
            });
        }

        // Add user to request object
        req.user = {
            id: user._id,
            email: user.email,
            username: user.username,
            role: user.role
        };

        next();
    } catch (error) {
        console.error('Auth middleware error:', error);
        res.status(401).json({
            success: false,
            message: 'Token không hợp lệ'
        });
    }
};

module.exports = auth;