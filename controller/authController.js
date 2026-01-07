const User = require('../model/User');
const { generateToken, hashPassword, comparePassword } = require('../config/auth');
const nodemailer = require('nodemailer');

// 1. Cấu hình lưu trữ OTP tạm thời kèm thời gian hết hạn
let otpCache = {};

// 2. Cấu hình gửi mail (Dung nhớ điền Email nhóm và App Password 16 số vào đây nhé)
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'dungle0223005@gmail.com',
        pass: 'bzanasdqqnxtgefa'
    }
});

// --- PHẦN 1: ĐĂNG KÝ (KHÔNG CẦN OTP) ---
const register = async(req, res) => {
    try {
        const { hoTen, mssv, email, password } = req.body;
        const existingUser = await User.findOne({ $or: [{ email }, { mssv }] });
        if (existingUser) return res.status(400).json({ message: 'MSSV hoặc Email đã tồn tại' });

        const hashedPassword = await hashPassword(password);
        const user = new User({
            hoTen,
            mssv,
            email,
            password: hashedPassword,
            role: 'user' // Mặc định là user
        });
        await user.save();

        res.status(201).json({ message: 'Đăng ký thành công!' });
    } catch (error) {
        res.status(500).json({ message: 'Lỗi server', error: error.message });
    }
};

// --- PHẦN 2: ĐĂNG NHẬP (TRẢ VỀ ROLE & AVATAR) ---
const login = async(req, res) => {
    try {
        const { mssv, password } = req.body;

        // Kiểm tra tài khoản admin cố định như bản cũ của Dung
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
            user: {
                id: user._id,
                hoTen: user.hoTen,
                role: user.role,
                mssv: user.mssv,
                avatar: user.avatar // Trả về avatar để hiện trên Profile
            }
        });
    } catch (error) {
        res.status(500).json({ message: 'Lỗi server' });
    }
};

// --- PHẦN 3: QUÊN MẬT KHẨU (GỬI OTP THEO MẪU RIÊNG) ---
const requestForgotPasswordOTP = async (req, res) => {
    try {
        const { email } = req.body;
        const user = await User.findOne({ email });

        if (!user) {
            return res.status(404).json({ message: 'Email này không tồn tại trong hệ thống MyUTE!' });
        }

        // Tạo mã OTP ngẫu nhiên 6 số
        const otp = Math.floor(100000 + Math.random() * 900000).toString();

        // Lưu mã vào cache, hết hạn sau 5 phút
        otpCache[email] = {
            otp: otp,
            expiresAt: Date.now() + 5 * 60 * 1000
        };

        // Gửi mail với mẫu nội dung Dung yêu cầu
        const mailOptions = {
            from: '"Đội ngũ MyUTE" <dungle0223005@gmail.com>',
            to: email, // Gửi đến đúng email người dùng nhập
            subject: 'Yêu cầu đặt lại mật khẩu tài khoản MyUTE',
            text: `Xin chào,\n\nBạn đã yêu cầu đặt lại mật khẩu cho tài khoản MyUTE của mình.\n\nMã OTP của bạn là: ${otp}\n\nMã này có hiệu lực trong vòng 5 phút.\n\nNếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\nTrân trọng,\nĐội ngũ MyUTE`
        };

        await transporter.sendMail(mailOptions);
        res.json({ message: 'Mã OTP đã được gửi về email của bạn!' });
    } catch (error) {
        res.status(500).json({ message: 'Lỗi gửi mail', error: error.message });
    }
};

// --- PHẦN 4: ĐỔI MẬT KHẨU MỚI (RESET PASSWORD) ---
const resetPassword = async (req, res) => {
    try {
        const { email, otp, newPassword } = req.body;
        const record = otpCache[email];

        if (!record || record.otp !== otp) {
            return res.status(400).json({ message: 'Mã OTP không chính xác!' });
        }

        // Kiểm tra thời hạn 5 phút
        if (Date.now() > record.expiresAt) {
            delete otpCache[email];
            return res.status(400).json({ message: 'Mã OTP đã hết hiệu lực (quá 5 phút)!' });
        }

        const hashedPassword = await hashPassword(newPassword);
        await User.findOneAndUpdate({ email }, { password: hashedPassword });

        delete otpCache[email]; // Xóa mã sau khi dùng thành công
        res.json({ message: 'Chúc mừng! Bạn đã đặt lại mật khẩu thành công.' });
    } catch (error) {
        res.status(500).json({ message: 'Lỗi server', error: error.message });
    }
};

module.exports = { register, login, requestForgotPasswordOTP, resetPassword };