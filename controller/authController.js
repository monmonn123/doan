const User = require('../model/User');
const { generateToken, hashPassword, comparePassword } = require('../config/auth');

const register = async(req, res) => {
    try {
        const { hoTen, mssv, email, password } = req.body;
        const existingUser = await User.findOne({ $or: [{ email }, { mssv }] });
        if (existingUser) return res.status(400).json({ message: 'MSSV hoặc Email đã tồn tại' });

        const hashedPassword = await hashPassword(password);
        const user = new User({ hoTen, mssv, email, password: hashedPassword });
        await user.save();

        res.status(201).json({ message: 'Đăng ký thành công!' });
    } catch (error) {
        res.status(500).json({ message: 'Lỗi server', error: error.message });
    }
};

const login = async(req, res) => {
    try {
        const { mssv, password } = req.body;

        // KIỂM TRA TÀI KHOẢN ADMIN CỐ ĐỊNH
        // Sử dụng một chuỗi 24 ký tự hex hợp lệ cho ObjectId
        if (mssv === 'admin' && password === 'admin') {
            const adminId = "000000000000000000000001"; 
            return res.json({
                token: generateToken(adminId),
                user: { id: adminId, hoTen: 'Quản trị viên', role: 'admin' }
            });
        }

        const user = await User.findOne({ mssv });
        if (!user || !(await comparePassword(password, user.password))) {
            return res.status(400).json({ message: 'Sai MSSV hoặc mật khẩu' });
        }

        const token = generateToken(user._id);
        res.json({
            token,
            user: { id: user._id, hoTen: user.hoTen, role: user.role, mssv: user.mssv }
        });
    } catch (error) {
        res.status(500).json({ message: 'Lỗi server' });
    }
};

module.exports = { register, login };