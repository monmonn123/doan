const Message = require('../model/Message');
const User = require('../model/User');
const mongoose = require('mongoose');

const searchUserByMssv = async(req, res) => {
    try {
        const { mssv } = req.params;
        const user = await User.findOne({ mssv }).select('-password');
        if (!user) return res.status(404).json({ success: false, message: 'Không tìm thấy sinh viên' });
        res.json({ success: true, user });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

const getMessages = async(req, res) => {
    try {
        const { senderId, receiverId } = req.params;
        const messages = await Message.find({
            $or: [
                { senderId, receiverId },
                { senderId: receiverId, receiverId: senderId }
            ]
        }).sort('createdAt');
        res.json({ success: true, messages });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

const sendMessage = async(req, res) => {
    try {
        const { senderId, receiverId, content } = req.body;
        const newMessage = new Message({ senderId, receiverId, content });
        await newMessage.save();
        res.status(201).json({ success: true, message: newMessage });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

const getConversationList = async(req, res) => {
    try {
        const userId = new mongoose.Types.ObjectId(req.params.userId);
        const conversations = await Message.aggregate([
            {
                $match: {
                    $or: [{ senderId: userId }, { receiverId: userId }]
                }
            },
            { $sort: { createdAt: -1 } },
            {
                $group: {
                    _id: {
                        $cond: [
                            { $eq: ["$senderId", userId] },
                            "$receiverId",
                            "$senderId"
                        ]
                    },
                    lastMessage: { $first: "$content" },
                    createdAt: { $first: "$createdAt" }
                }
            },
            {
                $lookup: {
                    from: "users",
                    localField: "_id",
                    foreignField: "_id",
                    as: "user"
                }
            },
            { $unwind: "$user" },
            {
                $project: {
                    _id: 1,
                    lastMessage: 1,
                    createdAt: 1,
                    "user.hoTen": 1,
                    "user.mssv": 1
                }
            },
            { $sort: { createdAt: -1 } }
        ]);
        res.json({ success: true, conversations });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

module.exports = { searchUserByMssv, getMessages, sendMessage, getConversationList };