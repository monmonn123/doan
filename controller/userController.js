const User = require('../model/User');

const getAllUsers = async (req, res) => {
    try {
        // Lấy tất cả user trừ admin hiện tại
        const users = await User.find({ role: { $ne: 'admin' } }).select('-password').sort({ mssv: 1 });
        res.json({ success: true, users });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

const updateRole = async (req, res) => {
    try {
        const { userId, newRole } = req.body;
        
        if (!['user', 'manager'].includes(newRole)) {
            return res.status(400).json({ success: false, message: 'Quyền không hợp lệ' });
        }

        const user = await User.findByIdAndUpdate(userId, { role: newRole }, { new: true });
        if (!user) return res.status(404).json({ success: false, message: 'Không tìm thấy người dùng' });

        res.json({ success: true, message: `Đã cập nhật quyền thành ${newRole}`, user });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

module.exports = { getAllUsers, updateRole };