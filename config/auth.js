const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

// Get JWT_SECRET from environment, with fallback for debugging
const JWT_SECRET = process.env.JWT_SECRET || 'your_jwt_secret_key';

// Fallback secrets for token migration (when JWT_SECRET changes)
const FALLBACK_SECRETS = [
    'your_jwt_secret_key', // Previous secret
    'DOAN1' // Another possible previous secret
];

console.log('Auth Config: JWT_SECRET loaded:', JWT_SECRET.substring(0, 5) + '...');

const hashPassword = async(password) => {
    const saltRounds = 10;
    return await bcrypt.hash(password, saltRounds);
};

const comparePassword = async(password, hashedPassword) => {
    return await bcrypt.compare(password, hashedPassword);
};

const generateToken = (userId) => {
    return jwt.sign({ userId }, JWT_SECRET, { expiresIn: '7d' });
};

const verifyToken = (token) => {
    // First try with current JWT_SECRET
    try {
        return jwt.verify(token, JWT_SECRET);
    } catch (error) {
        if (error.name === 'TokenExpiredError') {
            throw new Error('TOKEN_EXPIRED:Token đã hết hạn. Vui lòng đăng nhập lại.');
        }

        // Try with fallback secrets for token migration
        for (const secret of FALLBACK_SECRETS) {
            try {
                const decoded = jwt.verify(token, secret);
                console.log('Auth Config: Token verified with fallback secret:', secret.substring(0, 5) + '...');
                // Optional: Re-issue token with current secret
                return decoded;
            } catch (fallbackError) {
                // This secret didn't work, try next one
                continue;
            }
        }

        // No secret worked
        throw new Error('INVALID_TOKEN:Token không hợp lệ. Vui lòng đăng nhập lại. (Secret mismatch)');
    }
};

module.exports = {
    hashPassword,
    comparePassword,
    generateToken,
    verifyToken,
    JWT_SECRET
};