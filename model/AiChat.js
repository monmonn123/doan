const mongoose = require('mongoose');

const aiChatSchema = new mongoose.Schema({
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true,
        index: true
    },
    conversationId: {
        type: String,
        required: true,
        index: true,
        default: function() {
            return new mongoose.Types.ObjectId().toString();
        }
    },
    message: {
        type: String,
        required: true,
        trim: true
    },
    response: {
        type: String,
        required: true,
        trim: true
    },
    messageType: {
        type: String,
        enum: ['user', 'assistant'],
        default: 'user'
    },
    model: {
        type: String,
        default: 'default-ai-model'
    },
    tokens: {
        input: {
            type: Number,
            default: 0
        },
        output: {
            type: Number,
            default: 0
        },
        total: {
            type: Number,
            default: 0
        }
    },
    metadata: {
        duration: Number,
        temperature: {
            type: Number,
            default: 0.7
        },
        maxTokens: {
            type: Number,
            default: 1000
        }
    },
    isDeleted: {
        type: Boolean,
        default: false
    }
}, {
    timestamps: true
});

aiChatSchema.index({ userId: 1, conversationId: 1, createdAt: -1 });

aiChatSchema.statics.getConversationHistory = function(userId, conversationId, limit = 50, skip = 0) {
    return this.find({
            userId: userId,
            conversationId: conversationId,
            isDeleted: false
        })
        .sort({ createdAt: -1 })
        .limit(limit)
        .skip(skip);
};

// SỬA LỖI next is not a function TẠI ĐÂY
aiChatSchema.pre('save', function() {
    if (this.tokens) {
        this.tokens.total = (this.tokens.input || 0) + (this.tokens.output || 0);
    }
    // Trong Mongoose 5.x+, nếu không khai báo tham số 'next', 
    // hàm sẽ tự động hoàn thành khi kết thúc block code.
});

const AiChat = mongoose.model('AiChat', aiChatSchema);
module.exports = AiChat;