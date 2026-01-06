const mongoose = require('mongoose');

const questionSchema = new mongoose.Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    content: { type: String, required: true },
    image: { type: String }, 
    status: {
        type: String,
        enum: ['pending', 'approved', 'rejected'],
        default: 'pending'
    },
    likes: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }], 
    dislikes: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }], // Thêm trường dislike
    reports: [{ // Thêm trường báo cáo
        userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
        reason: String,
        createdAt: { type: Date, default: Date.now }
    }],
    comments: [{
        userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
        text: String,
        likes: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }], // Like cho comment
        dislikes: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }], // Dislike cho comment
        reports: [{ 
            userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
            reason: String,
            createdAt: { type: Date, default: Date.now }
        }],
        createdAt: { type: Date, default: Date.now }
    }]
}, { timestamps: true });

module.exports = mongoose.model('Question', questionSchema);