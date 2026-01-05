require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');

const User = require('./model/User');
const authRoutes = require('./routes/auth');
const aiChatRoutes = require('./routes/aiChat');
const app = express();

// Middleware
app.use(express.json());

// MongoDB connection
mongoose.connect(process.env.MONGO_URI || 'mongodb://localhost:27017/befinalproject')
    .then(() => console.log('MongoDB connected'))
    .catch(err => console.error('MongoDB connection error:', err));


// Routes
app.use('/api/auth', authRoutes);
app.use('/api/chat', aiChatRoutes);

app.get('/', (req, res) => {
    res.send('Server is running');
});

// Start server
const PORT = process.env.PORT || 4000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});