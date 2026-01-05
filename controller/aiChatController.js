const mongoose = require('mongoose');
const AiChat = require('../model/AiChat');
const User = require('../model/User');

// Mock AI response function (có thể thay thế bằng AI service thật)
const generateAIResponse = async(message, conversationHistory = []) => {
    // Simulate AI processing delay
    await new Promise(resolve => setTimeout(resolve, 1000 + Math.random() * 2000));

    // Simple mock responses based on keywords
    const responses = {
        'hello': 'Xin chào! Tôi là AI assistant. Tôi có thể giúp gì cho bạn?',
        'hi': 'Chào bạn! Bạn cần hỗ trợ gì hôm nay?',
        'help': 'Tôi có thể giúp bạn trả lời các câu hỏi, tư vấn, hoặc trò chuyện. Hãy hỏi tôi bất cứ điều gì!',
        'time': `Hiện tại là ${new Date().toLocaleString('vi-VN')}`,
        'weather': 'Tôi không thể kiểm tra thời tiết trực tiếp, nhưng bạn có thể tra cứu trên các ứng dụng thời tiết.',
        'code': 'Tôi có thể hỗ trợ bạn viết code. Hãy cho tôi biết bạn muốn viết ngôn ngữ lập trình nào?',
        'default': 'Cảm ơn bạn đã tin tưởng và trò chuyện với tôi. Đây là một phản hồi tự động từ AI. Bạn có thể hỏi thêm gì khác không?'
    };

    const lowerMessage = message.toLowerCase();

    for (const [keyword, response] of Object.entries(responses)) {
        if (lowerMessage.includes(keyword)) {
            return {
                response,
                model: 'mock-ai-v1',
                tokens: {
                    input: Math.ceil(message.length / 4), // Rough estimation
                    output: Math.ceil(response.length / 4)
                }
            };
        }
    }

    return {
        response: responses.default,
        model: 'mock-ai-v1',
        tokens: {
            input: Math.ceil(message.length / 4),
            output: Math.ceil(responses.default.length / 4)
        }
    };
};

// Gửi tin nhắn và nhận phản hồi từ AI
const sendMessage = async(req, res) => {
    try {
        const { message, conversationId } = req.body;
        const userId = req.user.id;

        // Validate input
        if (!message || message.trim().length === 0) {
            return res.status(400).json({
                success: false,
                message: 'Tin nhắn không được để trống'
            });
        }

        if (message.length > 1000) {
            return res.status(400).json({
                success: false,
                message: 'Tin nhắn quá dài (tối đa 1000 ký tự)'
            });
        }

        // Get conversation history for context
        const history = conversationId ?
            await AiChat.getConversationHistory(userId, conversationId, 10) : [];

        // Generate AI response
        const startTime = Date.now();
        const aiResult = await generateAIResponse(message, history);
        const duration = Date.now() - startTime;

        // Create new chat record
        const newChat = new AiChat({
            userId,
            conversationId: conversationId || new mongoose.Types.ObjectId().toString(),
            message: message.trim(),
            response: aiResult.response,
            messageType: 'user',
            model: aiResult.model,
            tokens: {
                ...aiResult.tokens,
                total: aiResult.tokens.input + aiResult.tokens.output
            },
            metadata: {
                duration,
                temperature: 0.7,
                maxTokens: 1000
            }
        });

        await newChat.save();

        res.status(201).json({
            success: true,
            data: {
                chatId: newChat._id,
                conversationId: newChat.conversationId,
                message: newChat.message,
                response: newChat.response,
                timestamp: newChat.createdAt,
                model: newChat.model,
                tokens: newChat.tokens,
                metadata: newChat.metadata
            }
        });

    } catch (error) {
        console.error('Send message error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi server khi gửi tin nhắn',
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
    }
};

// Lấy lịch sử chat
const getChatHistory = async(req, res) => {
    try {
        const { conversationId } = req.params;
        const { page = 1, limit = 20 } = req.query;
        const userId = req.user.id;

        const skip = (parseInt(page) - 1) * parseInt(limit);

        const chats = await AiChat.getConversationHistory(
            userId,
            conversationId,
            parseInt(limit),
            skip
        );

        const total = await AiChat.countDocuments({
            userId,
            conversationId,
            isDeleted: false
        });

        res.json({
            success: true,
            data: {
                chats,
                pagination: {
                    currentPage: parseInt(page),
                    totalPages: Math.ceil(total / parseInt(limit)),
                    totalItems: total,
                    itemsPerPage: parseInt(limit)
                }
            }
        });

    } catch (error) {
        console.error('Get chat history error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi server khi lấy lịch sử chat',
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
    }
};

// Lấy danh sách cuộc trò chuyện của user
const getUserConversations = async(req, res) => {
    try {
        const userId = req.user.id;

        const conversations = await AiChat.getUserConversations(userId);

        res.json({
            success: true,
            data: conversations
        });

    } catch (error) {
        console.error('Get user conversations error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi server khi lấy danh sách cuộc trò chuyện',
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
    }
};

// Xóa cuộc trò chuyện
const deleteConversation = async(req, res) => {
    try {
        const { conversationId } = req.params;
        const userId = req.user.id;

        // Soft delete all messages in the conversation
        await AiChat.updateMany({ userId, conversationId }, { isDeleted: true });

        res.json({
            success: true,
            message: 'Đã xóa cuộc trò chuyện thành công'
        });

    } catch (error) {
        console.error('Delete conversation error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi server khi xóa cuộc trò chuyện',
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
    }
};

// Xóa tin nhắn cụ thể
const deleteMessage = async(req, res) => {
    try {
        const { chatId } = req.params;
        const userId = req.user.id;

        const chat = await AiChat.findOne({ _id: chatId, userId });

        if (!chat) {
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy tin nhắn'
            });
        }

        await chat.softDelete();

        res.json({
            success: true,
            message: 'Đã xóa tin nhắn thành công'
        });

    } catch (error) {
        console.error('Delete message error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi server khi xóa tin nhắn',
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
    }
};

// Thống kê chat của user
const getChatStats = async(req, res) => {
    try {
        const userId = req.user.id;

        const stats = await AiChat.aggregate([
            { $match: { userId: mongoose.Types.ObjectId(userId), isDeleted: false } },
            {
                $group: {
                    _id: null,
                    totalMessages: { $sum: 1 },
                    totalConversations: { $addToSet: '$conversationId' },
                    totalTokens: { $sum: '$tokens.total' },
                    avgResponseTime: { $avg: '$metadata.duration' }
                }
            },
            {
                $project: {
                    _id: 0,
                    totalMessages: 1,
                    totalConversations: { $size: '$totalConversations' },
                    totalTokens: 1,
                    avgResponseTime: { $round: ['$avgResponseTime', 2] }
                }
            }
        ]);

        res.json({
            success: true,
            data: stats[0] || {
                totalMessages: 0,
                totalConversations: 0,
                totalTokens: 0,
                avgResponseTime: 0
            }
        });

    } catch (error) {
        console.error('Get chat stats error:', error);
        res.status(500).json({
            success: false,
            message: 'Lỗi server khi lấy thống kê chat',
            error: process.env.NODE_ENV === 'development' ? error.message : undefined
        });
    }
};

module.exports = {
    sendMessage,
    getChatHistory,
    getUserConversations,
    deleteConversation,
    deleteMessage,
    getChatStats
};