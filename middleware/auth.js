const jwt = require('jsonwebtoken');
const { verifyToken, JWT_SECRET } = require('../config/auth');
const User = require('../model/User');

// JWT_SECRET is now imported from config/auth.js to ensure consistency

const auth = async(req, res, next) => {
    console.log('--- AUTH CHECKING ---');
    try {
        const authHeader = req.header('Authorization');

        if (!authHeader) {
            console.log('Auth Error: No Authorization header provided');
            return res.status(401).json({
                success: false,
                message: 'Không có token, truy cập bị từ chối'
            });
        }

        // Kiểm tra định dạng Bearer token
        if (!authHeader.startsWith('Bearer ')) {
            console.log('Auth Error: Invalid Authorization format');
            return res.status(401).json({
                success: false,
                message: 'Định dạng token không hợp lệ. Sử dụng: Bearer <token>'
            });
        }

        const token = authHeader.slice(7, authHeader.length);

        if (!token || token.trim().length === 0) {
            console.log('Auth Error: Token is empty');
            return res.status(401).json({
                success: false,
                message: 'Token trống'
            });
        }

        console.log('Auth Info: Token received:', token.substring(0, 20) + '...');
        console.log('Auth Info: JWT_SECRET used:', JWT_SECRET.substring(0, 5) + '...');

        // DEBUG: Try to decode without verification first
        try {
            const decodedDebug = jwt.decode(token);
            console.log('Auth Debug: Token payload (decoded without verification):', decodedDebug);
        } catch (decodeError) {
            console.log('Auth Debug: Failed to decode token:', decodeError.message);
        }

        let decoded;
        try {
            decoded = verifyToken(token);
            console.log('Auth Info: Token decoded successfully, userId:', decoded.userId);
        } catch (error) {
            console.error('Auth middleware error:', error.message);

            // Xử lý các loại lỗi token cụ thể
            if (error.message.startsWith('TOKEN_EXPIRED:')) {
                console.log('Auth Error: Token expired');
                const message = error.message.replace('TOKEN_EXPIRED:', '');
                return res.status(401).json({
                    success: false,
                    message: message,
                    errorType: 'TOKEN_EXPIRED'
                });
            }

            if (error.message.startsWith('INVALID_TOKEN:')) {
                console.log('Auth Error: Invalid token signature');
                const message = error.message.replace('INVALID_TOKEN:', '');
                return res.status(401).json({
                    success: false,
                    message: message,
                    errorType: 'INVALID_TOKEN'
                });
            }

            console.log('Auth Error: Unknown token error');
            return res.status(401).json({
                success: false,
                message: error.message || 'Token không hợp lệ',
                errorType: 'UNKNOWN_ERROR'
            });
        }

        // KIỂM TRA NẾU LÀ ADMIN CỐ ĐỊNH
        if (decoded.userId === "000000000000000000000001") {
            console.log('Auth Info: Admin user detected');
            req.user = {
                id: "000000000000000000000001",
                hoTen: "Quản trị viên",
                role: "admin"
            };
            return next();
        }

        const user = await User.findById(decoded.userId).select('-password');

        if (!user) {
            console.log('Auth Error: User not found for userId:', decoded.userId);
            return res.status(401).json({
                success: false,
                message: 'Token không hợp lệ - User không tồn tại'
            });
        }

        console.log('Auth Info: User authenticated:', user.email);
        req.user = {
            id: user._id,
            email: user.email,
            username: user.username,
            role: user.role
        };

        next();
    } catch (error) {
        console.error('Auth middleware error:', error.message);
        res.status(401).json({
            success: false,
            message: 'Lỗi xác thực: ' + error.message
        });
    }
};

module.exports = auth;