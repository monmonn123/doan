const User = require('../model/User');

const searchForAdmin = async (req, res) => {
    try {
        // Tìm người dùng có vai trò là 'admin' hoặc 'manager'
        const admin = await User.findOne({ role: { $in: ['admin', 'manager'] } }).select('-password');
        if (!admin) {
            return res.status(404).json({ success: false, message: 'Không tìm thấy quản trị viên' });
        }
        res.json({ success: true, user: admin }); // Trả về user thay vì admin để đồng bộ
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

const searchUserByMssv = async (req, res) => {
    try {
        const { mssv } = req.params;
        const user = await User.findOne({ mssv: mssv }).select('-password');
        if (!user) {
            return res.status(404).json({ success: false, message: 'Không tìm thấy người dùng' });
        }
        res.json({ success: true, user });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};


module.exports = { searchForAdmin, searchUserByMssv };