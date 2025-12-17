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
        duration: Number, // Thời gian xử lý (milliseconds)
        temperature: {
            type: Number,
            default: 0.7
        },
        maxTokens: {
            type: Number,
            default: 1000
        }
    },
    isActive: {
        type: Boolean,
        default: true
    },
    isDeleted: {
        type: Boolean,
        default: false
    }
}, {
    timestamps: true
});

// Compound index for better query performance
aiChatSchema.index({ userId: 1, conversationId: 1, createdAt: -1 });
aiChatSchema.index({ userId: 1, createdAt: -1 });
aiChatSchema.index({ conversationId: 1, createdAt: 1 });

// Virtual for conversation count
aiChatSchema.virtual('messageCount', {
    ref: 'AiChat',
    localField: 'conversationId',
    foreignField: 'conversationId',
    count: true
});

// Static method to get conversation history
aiChatSchema.statics.getConversationHistory = function(userId, conversationId, limit = 50, skip = 0) {
    return this.find({
            userId: userId,
            conversationId: conversationId,
            isDeleted: false
        })
        .sort({ createdAt: -1 })
        .limit(limit)
        .skip(skip)
        .select('-__v');
};

// Static method to get user conversations list
aiChatSchema.statics.getUserConversations = function(userId) {
    return this.aggregate([{
            $match: {
                userId: mongoose.Types.ObjectId(userId),
                isDeleted: false
            }
        },
        {
            $group: {
                _id: '$conversationId',
                lastMessage: { $last: '$message' },
                lastResponse: { $last: '$response' },
                messageCount: { $sum: 1 },
                firstMessageAt: { $first: '$createdAt' },
                lastMessageAt: { $last: '$createdAt' },
                model: { $last: '$model' }
            }
        },
        {
            $sort: { lastMessageAt: -1 }
        }
    ]);
};

// Instance method to soft delete
aiChatSchema.methods.softDelete = function() {
    this.isDeleted = true;
    return this.save();
};

// Pre-save middleware to calculate total tokens
aiChatSchema.pre('save', function(next) {
    if (this.tokens) {
        this.tokens.total = (this.tokens.input || 0) + (this.tokens.output || 0);
    }
    next();
});

const AiChat = mongoose.model('AiChat', aiChatSchema);

module.exports = AiChat;