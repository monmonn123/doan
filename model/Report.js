const mongoose = require('mongoose');

const reportSchema = new mongoose.Schema({
    reporterId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    reportedUserId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    reason: {
        type: String,
        enum: ['spam', 'harassment', 'inappropriate_content', 'fake_account', 'other'],
        required: true
    },
    description: {
        type: String,
        maxLength: 1000
    },
    status: {
        type: String,
        enum: ['pending', 'reviewed', 'resolved', 'rejected'],
        default: 'pending'
    },
    adminNote: {
        type: String,
        maxLength: 500
    },
    createdAt: {
        type: Date,
        default: Date.now
    },
    updatedAt: {
        type: Date,
        default: Date.now
    }
});

// Prevent duplicate reports from same user for same target
reportSchema.index({ reporterId: 1, reportedUserId: 1 });

const Report = mongoose.model('Report', reportSchema);
module.exports = Report;