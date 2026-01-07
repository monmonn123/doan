const express = require('express');
const router = express.Router();
const { getAllUsers, updateRole, getUserProfile, updateUserProfile } = require('../controller/userController');
const auth = require('../middleware/auth');

router.use(auth);

router.get('/all', getAllUsers);
router.get('/profile/:id', getUserProfile);
router.put('/profile/:id', updateUserProfile); // Thêm route này
router.put('/update-role', updateRole);

module.exports = router;