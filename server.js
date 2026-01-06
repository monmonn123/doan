require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const multer = require('multer');
const path = require('path');

const User = require('./model/User');
const Question = require('./model/Question'); // Đưa lên đầu cho sạch
const authRoutes = require('./routes/auth');
const aiChatRoutes = require('./routes/aiChat');

const app = express();

// --- CẤU HÌNH LƯU FILE ---
const storage = multer.diskStorage({
    destination: (req, file, cb) => { cb(null, 'uploads/'); },
    filename: (req, file, cb) => { cb(null, Date.now() + '-' + file.originalname); }
});
const upload = multer({ storage: storage });
app.use('/uploads', express.static('uploads'));

// Middleware
app.use(express.json());

// MongoDB connection
mongoose.connect(process.env.MONGO_URI || 'mongodb://localhost:27017/befinalproject')
    .then(() => console.log('MongoDB connected'))
    .catch(err => console.error('MongoDB connection error:', err));

// Routes cũ giữ nguyên
app.use('/api/auth', authRoutes);
app.use('/api/chat', aiChatRoutes);

// --- CÁC API BLOG Q&A (ĐÃ ĐƯA RA NGOÀI VÀ ĐÚNG VỊ TRÍ) ---

// 1. API CHO SINH VIÊN: ĐĂNG BÀI
app.post('/api/questions/post', upload.fields([{ name: 'image' }, { name: 'file' }]), async (req, res) => {
    try {
        const newQuestion = new Question({
            userId: req.body.userId,
            content: req.body.content,
            image: req.files['image'] ? req.files['image'][0].path : null,
            file: req.files['file'] ? req.files['file'][0].path : null
        });
        await newQuestion.save();
        res.status(201).json({ message: "Đã gửi bài, vui lòng đợi Admin duyệt!" });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// 2. API CHO ADMIN: LẤY DANH SÁCH CHỜ DUYỆT
app.get('/api/questions/pending', async (req, res) => {
    try {
        const pendingPosts = await Question.find({ status: 'pending' }).populate('userId', 'username');
        res.json(pendingPosts);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// 3. API CHO ADMIN: DUYỆT HOẶC HỦY BÀI
app.put('/api/questions/approve/:id', async (req, res) => {
    const { action, message } = req.body;
    try {
        await Question.findByIdAndUpdate(req.params.id, {
            status: action,
            adminMessage: message
        });
        res.json({ message: `Đã ${action === 'approved' ? 'Duyệt' : 'Hủy'} bài viết!` });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// 4. API CHO TẤT CẢ: LẤY BÀI CÔNG KHAI
app.get('/api/questions/public', async (req, res) => {
    try {
        const publicPosts = await Question.find({ status: 'approved' })
            .populate('userId', 'username')
            .sort({ createdAt: -1 });
        res.json(publicPosts);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// --- API CHO TẤT CẢ: BÌNH LUẬN VÀO BÀI VIẾT ---
app.post('/api/questions/comment/:id', async (req, res) => {
    try {
        const { userId, text } = req.body;
        const question = await Question.findById(req.params.id);

        if (!question) return res.status(404).json({ message: "Không tìm thấy bài viết!" });

        // Thêm comment mới vào mảng comments của bài viết
        question.comments.push({ userId, text });
        await question.save();

        res.status(200).json({ message: "Đã đăng bình luận!" });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// --- API: THẢ TIM / BỎ TIM ---
app.put('/api/questions/like/:id', async (req, res) => {
    try {
        const { userId }  = req.body;
        const question = await Question.findById(req.params.id);
        if (!question.likes.includes(userId)) {
            await question.updateOne({ $push: { likes: userId } });
            res.status(200).json({ message: "Đã thích bài viết!" });
        } else {
            await question.updateOne({ $pull: { likes: userId } });
            res.status(200).json({ message: "Đã bỏ thích!" });
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});
// --- KHỞI CHẠY SERVER ---
const PORT = process.env.PORT || 4000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});