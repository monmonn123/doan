const jwt = require('jsonwebtoken');
const { verifyToken } = require('../config/auth');
const User = require('../model/User');

const auth = async(req, res, next) => {
    console.log('--- AUTH CHECKING ---'); // Thêm dòng này để bạn kiểm tra server đã nhận code mới chưa
    try {
        const authHeader = req.header('Authorization');

        if (!authHeader) {
            return res.status(401).json({
                success: false,
                message: 'Không có token, truy cập bị từ chối'
            });
        }

        const token = authHeader.startsWith('Bearer ') ?
            authHeader.slice(7, authHeader.length) :
            authHeader;

        const decoded = verifyToken(token);
        const user = await User.findById(decoded.userId).select('-password');

        if (!user) {
            return res.status(401).json({
                success: false,
                message: 'Token không hợp lệ'
            });
        }

        // ĐÃ XÓA HOÀN TOÀN ĐOẠN KIỂM TRA isActive TẠI ĐÂY

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