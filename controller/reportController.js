const Report = require('../model/Report');
const User = require('../model/User');

// Report reasons enum for reference
const REPORT_REASONS = [
    { value: 'spam', label: 'Spam / Quảng cáo' },
    { value: 'harassment', label: 'Quấy rối / Xúc phạm' },
    { value: 'inappropriate_content', label: 'Nội dung không phù hợp' },
    { value: 'fake_account', label: 'Tài khoản giả mạo' },
    { value: 'other', label: 'Khác' }
];

// Create a new report
const createReport = async(req, res) => {
    try {
        const { reportedUserId, reason, description } = req.body;
        const reporterId = req.user.id;

        // Validate input
        if (!reportedUserId || !reason) {
            return res.status(400).json({
                success: false,
                message: 'Vui lòng cung cấp người dùng bị báo cáo và lý do'
            });
        }

        // Cannot report yourself
        if (reportedUserId === reporterId) {
            return res.status(400).json({
                success: false,
                message: 'Bạn không thể tự báo cáo mình'
            });
        }

        // Check if reported user exists
        const reportedUser = await User.findById(reportedUserId);
        if (!reportedUser) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy người dùng bị báo cáo'
            });
        }

        // Check if already reported by this user
        const existingReport = await Report.findOne({ reporterId, reportedUserId });
        if (existingReport) {
            return res.status(400).json({
                success: false,
                message: 'Bạn đã báo cáo người dùng này trước đó'
            });
        }

        // Validate reason
        const validReasons = REPORT_REASONS.map(r => r.value);
        if (!validReasons.includes(reason)) {
            return res.status(400).json({
                success: false,
                message: 'Lý do báo cáo không hợp lệ'
            });
        }

        const newReport = new Report({
            reporterId,
            reportedUserId,
            reason,
            description: description || ''
        });

        await newReport.save();

        res.status(201).json({
            success: true,
            message: 'Báo cáo đã được gửi. Cảm ơn bạn đã phản hồi!',
            data: {
                reportId: newReport._id,
                reason: newReport.reason,
                status: newReport.status,
                createdAt: newReport.createdAt
            }
        });

    } catch (error) {
        console.error('Create report error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi gửi báo cáo: ' + error.message
        });
    }
};

// Get user's reports (reports they made)
const getMyReports = async(req, res) => {
    try {
        const userId = req.user.id;

        const reports = await Report.find({ reporterId: userId })
            .populate('reportedUserId', 'hoTen mssv email avatar')
            .sort({ createdAt: -1 });

        res.json({
            success: true,
            data: reports
        });

    } catch (error) {
        console.error('Get my reports error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy danh sách báo cáo: ' + error.message
        });
    }
};

// Get public profile of a user
const getUserProfile = async(req, res) => {
    try {
        const { userId } = req.params;
        const currentUserId = req.user.id;

        const user = await User.findById(userId).select('-password -email');

        if (!user) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy người dùng'
            });
        }

        // Get user's activity stats (questions asked, etc.)
        const Question = require('../model/Question');
        const AiChat = require('../model/AiChat');

        const questionsCount = await Question.countDocuments({
            userId: userId,
            status: 'approved'
        });

        const chatsCount = await AiChat.countDocuments({
            userId: userId,
            isDeleted: false
        });

        // Check if current user has reported this user
        const hasReported = await Report.exists({
            reporterId: currentUserId,
            reportedUserId: userId
        });

        res.json({
            success: true,
            data: {
                user: {
                    id: user._id,
                    hoTen: user.hoTen,
                    mssv: user.mssv,
                    avatar: user.avatar,
                    role: user.role,
                    createdAt: user.createdAt
                },
                stats: {
                    questionsAsked: questionsCount,
                    totalChats: chatsCount
                },
                isReported: !!hasReported
            }
        });

    } catch (error) {
        console.error('Get user profile error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy thông tin người dùng: ' + error.message
        });
    }
};

// Admin: Get all pending reports
const getAllReports = async(req, res) => {
    try {
        const { status = 'pending', page = 1, limit = 20 } = req.query;
        const skip = (parseInt(page) - 1) * parseInt(limit);

        const query = {};
        if (status !== 'all') {
            query.status = status;
        }

        const reports = await Report.find(query)
            .populate('reporterId', 'hoTen mssv')
            .populate('reportedUserId', 'hoTen mssv email avatar')
            .sort({ createdAt: -1 })
            .skip(skip)
            .limit(parseInt(limit));

        const total = await Report.countDocuments(query);

        res.json({
            success: true,
            data: {
                reports,
                pagination: {
                    currentPage: parseInt(page),
                    totalPages: Math.ceil(total / parseInt(limit)),
                    totalItems: total,
                    itemsPerPage: parseInt(limit)
                }
            }
        });

    } catch (error) {
        console.error('Get all reports error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi lấy danh sách báo cáo: ' + error.message
        });
    }
};

// Admin: Update report status
const updateReportStatus = async(req, res) => {
    try {
        const { reportId } = req.params;
        const { status, adminNote } = req.body;

        const validStatuses = ['pending', 'reviewed', 'resolved', 'rejected'];
        if (!validStatuses.includes(status)) {
            return res.status(400).json({
                success: false,
                message: 'Trạng thái không hợp lệ'
            });
        }

        const report = await Report.findByIdAndUpdate(
                reportId, {
                    status,
                    adminNote,
                    updatedAt: Date.now()
                }, { new: true }
            ).populate('reporterId', 'hoTen mssv')
            .populate('reportedUserId', 'hoTen mssv email avatar');

        if (!report) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy báo cáo'
            });
        }

        // If report is resolved, optionally take action on reported user
        if (status === 'resolved') {
            // Could ban user, send warning, etc.
            await User.findByIdAndUpdate(report.reportedUserId._id, {
                $inc: { reportCount: 1 }
            });
        }

        res.json({
            success: true,
            message: 'Cập nhật trạng thái báo cáo thành công',
            data: report
        });

    } catch (error) {
        console.error('Update report status error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi khi cập nhật báo cáo: ' + error.message
        });
    }
};

// Get report reasons list
const getReportReasons = async(req, res) => {
    res.json({
        success: true,
        data: REPORT_REASONS
    });
};

module.exports = {
    createReport,
    getMyReports,
    getUserProfile,
    getAllReports,
    updateReportStatus,
    getReportReasons,
    REPORT_REASONS
};