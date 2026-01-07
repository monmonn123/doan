const User = require('../model/User');

const getAllUsers = async (req, res) => {
    try {
        const users = await User.find({ role: { $ne: 'admin' } }).select('-password').sort({ mssv: 1 });
        res.json({ success: true, users });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

const getUserProfile = async (req, res) => {
    try {
        const user = await User.findById(req.params.id).select('-password');
        if (!user) {
            return res.status(404).json({ success: false, message: 'Không tìm thấy người dùng' });
        }
        res.json({ success: true, user });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

const updateUserProfile = async (req, res) => {
    try {
        const { hoTen, email, mssv } = req.body;
        const updatedUser = await User.findByIdAndUpdate(
            req.params.id,
            { hoTen, email, mssv },
            { new: true }
        ).select('-password');
        if (!updatedUser) {
            return res.status(404).json({ success: false, message: 'Không tìm thấy người dùng' });
        }
        res.json({ success: true, message: 'Cập nhật thành công', user: updatedUser });
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

module.exports = { getAllUsers, getUserProfile, updateUserProfile, updateRole };