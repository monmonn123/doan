const express = require('express');
const router = express.Router();
const { getAllUsers, updateRole } = require('../controller/userController');
const auth = require('../middleware/auth');

router.use(auth);

// Middleware to check admin role
const requireAdmin = (req, res, next) => {
    if (req.user.role !== 'admin') {
        return res.status(403).json({ success: false, message: "Yêu cầu quyền admin." });
    }
    next();
};

// All user management routes require admin
router.get('/all', requireAdmin, getAllUsers);
router.put('/update-role', requireAdmin, updateRole);

module.exports = router;