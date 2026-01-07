const jwt = require('jsonwebtoken');
const { verifyToken } = require('../config/auth');
const User = require('../model/User');

const auth = async(req, res, next) => {
    console.log('--- AUTH CHECKING ---');
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
        
        // KIỂM TRA NẾU LÀ ADMIN CỐ ĐỊNH
        if (decoded.userId === "000000000000000000000001") {
            req.user = {
                id: "000000000000000000000001",
                hoTen: "Quản trị viên",
                role: "admin"
            };
            return next();
        }

        const user = await User.findById(decoded.userId).select('-password');

        if (!user) {
            return res.status(401).json({
                success: false,
                message: 'Token không hợp lệ - User không tồn tại'
            });
        }

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