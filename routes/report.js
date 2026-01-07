const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const {
    createReport,
    getMyReports,
    getUserProfile,
    getAllReports,
    updateReportStatus,
    getReportReasons
} = require('../controller/reportController');

// All routes require authentication
router.use(auth);

// User routes
router.post('/create', createReport);
router.get('/my-reports', getMyReports);
router.get('/reasons', getReportReasons);
router.get('/profile/:userId', getUserProfile);

// Admin routes
router.get('/admin/all', getAllReports);
router.put('/admin/:reportId', updateReportStatus);

module.exports = router;