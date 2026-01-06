const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    hoTen: {
        type: String,
        required: true,
        trim: true
    },
    mssv: {
        type: String,
        required: true,
        unique: true,
        trim: true
    },
    email: {
        type: String,
        required: true,
        unique: true,
        lowercase: true,
        trim: true
    },
    password: {
        type: String,
        required: true
    },
    role: {
        type: String,
        enum: ['user', 'admin'],
        default: 'user'
    },
    isActive: { // Thêm trường này để tránh lỗi 401
        type: Boolean,
        default: true
    },
    avatar: {
        type: String,
        default: ""
    }
}, {
    timestamps: true
});

const User = mongoose.model('User', userSchema);
module.exports = User;