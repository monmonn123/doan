require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const multer = require('multer');
const path = require('path');

// Models
const User = require('./model/User');

// Routes
const authRoutes = require('./routes/auth');
const aiChatRoutes = require('./routes/aiChat');
const messageRoutes = require('./routes/message');
const userRoutes = require('./routes/user');
const notificationRoutes = require('./routes/notification');
const questionRoutes = require('./routes/question'); // IMPORT ROUTE MỚI
const documentRoutes = require('./routes/document'); // IMPORT ROUTE MỚI

const app = express();

// --- Middlewares ---
const storage = multer.diskStorage({
    destination: (req, file, cb) => { cb(null, 'uploads/'); },
    filename: (req, file, cb) => { cb(null, Date.now() + '-' + file.originalname); }
});
const upload = multer({ storage: storage });
app.use('/uploads', express.static('uploads'));
app.use(express.json());

// --- Database Connection ---
mongoose.connect(process.env.MONGO_URI || 'mongodb://localhost:27017/befinalproject')
    .then(() => console.log('MongoDB connected'))
    .catch(err => console.error('MongoDB connection error:', err));

// --- API Routes ---
app.use('/api/auth', authRoutes);
app.use('/api/chat', aiChatRoutes);
app.use('/api/messages', messageRoutes);
app.use('/api/users', userRoutes);
app.use('/api/notifications', notificationRoutes);
app.use('/api/questions', questionRoutes); // SỬ DỤNG ROUTE MỚI
app.use('/api/documents', documentRoutes); // SỬ DỤNG ROUTE MỚI

// --- Server Start ---
const PORT = process.env.PORT || 4000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
