const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    hoTen: { // Đổi từ firstName/lastName sang hoTen cho khớp ảnh DB
        type: String,
        required: true,
        trim: true
    },
    mssv: { // Thêm trường mssv như trong ảnh DB
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
    avatar: {
        type: String,
        default: ""
    }
}, {
    timestamps: true
});

const User = mongoose.model('User', userSchema);
module.exports = User;