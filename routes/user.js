const express = require('express');
const router = express.Router();
const { getAllUsers, updateRole } = require('../controller/userController');
const auth = require('../middleware/auth');

router.use(auth);

router.get('/all', getAllUsers);
router.put('/update-role', updateRole);

module.exports = router;