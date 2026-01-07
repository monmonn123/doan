require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const multer = require('multer');
const path = require('path');

const User = require('./model/User');
const Question = require('./model/Question');
const authRoutes = require('./routes/auth');
const aiChatRoutes = require('./routes/aiChat');
const messageRoutes = require('./routes/message');
const userRoutes = require('./routes/user');
const reportRoutes = require('./routes/report');

const app = express();

const storage = multer.diskStorage({
    destination: (req, file, cb) => { cb(null, 'uploads/'); },
    filename: (req, file, cb) => { cb(null, Date.now() + '-' + file.originalname); }
});
const upload = multer({ storage: storage });
app.use('/uploads', express.static('uploads'));

app.use(express.json());

mongoose.connect(process.env.MONGO_URI || 'mongodb://localhost:27017/befinalproject')
    .then(() => console.log('MongoDB connected'))
    .catch(err => console.error('MongoDB connection error:', err));

app.use('/api/auth', authRoutes);
app.use('/api/chat', aiChatRoutes);
app.use('/api/messages', messageRoutes);
app.use('/api/users', userRoutes);
app.use('/api/reports', reportRoutes);

app.post('/api/questions/post', upload.fields([{ name: 'image' }, { name: 'file' }]), async(req, res) => {
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

app.get('/api/questions/pending', async(req, res) => {
    try {
        const pendingPosts = await Question.find({ status: 'pending' })
            .populate('userId', 'mssv hoTen')
            .populate('comments.userId', 'mssv hoTen');
        res.json(pendingPosts);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.put('/api/questions/approve/:id', async(req, res) => {
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

app.get('/api/questions/public', async(req, res) => {
    try {
        const publicPosts = await Question.find({ status: 'approved' })
            .populate('userId', 'mssv hoTen')
            .populate('comments.userId', 'mssv hoTen')
            .sort({ createdAt: -1 });
        res.json(publicPosts);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.post('/api/questions/comment/:id', async(req, res) => {
    try {
        const { userId, text } = req.body;
        const question = await Question.findById(req.params.id);
        if (!question) return res.status(404).json({ message: "Không tìm thấy bài viết!" });
        question.comments.push({ userId, text });
        await question.save();
        res.status(200).json({ message: "Đã đăng bình luận!" });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// --- LOGIC LIKE KIỂU FACEBOOK ---
app.put('/api/questions/like/:id', async(req, res) => {
    try {
        const { userId } = req.body;
        const question = await Question.findById(req.params.id);
        if (!question) return res.status(404).json({ message: "Không tìm thấy bài viết" });

        const isLiked = question.likes.includes(userId);
        if (isLiked) {
            // Nếu đã Like rồi -> Bỏ Like
            await Question.findByIdAndUpdate(req.params.id, { $pull: { likes: userId } });
        } else {
            // Nếu chưa Like -> Thêm Like VÀ tự động bỏ Dislike
            await Question.findByIdAndUpdate(req.params.id, {
                $push: { likes: userId },
                $pull: { dislikes: userId }
            });
        }

        const updated = await Question.findById(req.params.id);
        res.json({
            likesCount: updated.likes.length,
            dislikesCount: updated.dislikes.length,
            isLiked: !isLiked
        });
    } catch (err) { res.status(500).json({ error: err.message }); }
});

// --- LOGIC DISLIKE TƯƠNG TỰ ---
app.put('/api/questions/dislike/:id', async(req, res) => {
    try {
        const { userId } = req.body;
        const question = await Question.findById(req.params.id);
        const isDisliked = question.dislikes.includes(userId);

        if (isDisliked) {
            await Question.findByIdAndUpdate(req.params.id, { $pull: { dislikes: userId } });
        } else {
            await Question.findByIdAndUpdate(req.params.id, {
                $push: { dislikes: userId },
                $pull: { likes: userId } // Bấm Dislike thì tự bỏ Like
            });
        }
        const updated = await Question.findById(req.params.id);
        res.json({ likesCount: updated.likes.length, dislikesCount: updated.dislikes.length });
    } catch (err) { res.status(500).json({ error: err.message }); }
});

// server.js
app.put('/api/users/update-profile/:id', upload.single('avatar'), async(req, res) => {
    try {
        // Nhận tất cả thông tin từ body gửi lên
        const { hoTen, email, mssv } = req.body;
        const updateFields = { hoTen, email, mssv };

        // Nếu có ảnh mới thì mới cập nhật đường dẫn ảnh
        if (req.file) {
            updateFields.avatar = req.file.path;
        }

        const updatedUser = await User.findByIdAndUpdate(
            req.params.id,
            updateFields, { new: true } // Trả về bản ghi đã cập nhật
        ).select('-password');

        res.json({ success: true, user: updatedUser });
    } catch (err) {
        res.status(500).json({ success: false, error: err.message });
    }
});

// Lấy cổng từ file .env hoặc mặc định là 4000
const PORT = process.env.PORT || 4000;

// Bắt đầu lắng nghe các kết nối
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});