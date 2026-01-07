const mongoose = require('mongoose');
const AiChat = require('../model/AiChat');
const User = require('../model/User');
const https = require('https');

// Web Search Function using DuckDuckGo Instant Answer API (Free, no API key needed)
const searchWeb = async(query) => {
    return new Promise((resolve) => {
        const encodedQuery = encodeURIComponent(query);
        const url = `https://api.duckduckgo.com/?q=${encodedQuery}&format=json&no_html=1&skip_disambig=1`;

        https.get(url, (res) => {
            let data = '';

            res.on('data', (chunk) => {
                data += chunk;
            });

            res.on('end', () => {
                try {
                    const result = JSON.parse(data);
                    resolve({
                        abstract: result.Abstract || '',
                        answer: result.Answer || '',
                        relatedTopics: (result.RelatedTopics || []).slice(0, 3).map(t => t.Text).filter(Boolean)
                    });
                } catch (error) {
                    resolve({ abstract: '', answer: '', relatedTopics: [] });
                }
            });
        }).on('error', (error) => {
            console.error('Web search error:', error);
            resolve({ abstract: '', answer: '', relatedTopics: [] });
        });

        // Timeout after 5 seconds
        setTimeout(() => {
            resolve({ abstract: '', answer: '', relatedTopics: [] });
        }, 5000);
    });
};

// Wikipedia Search (Free API)
const searchWikipedia = async(query) => {
    return new Promise((resolve) => {
        const encodedQuery = encodeURIComponent(query);
        const url = `https://en.wikipedia.org/api/rest_v1/page/summary/${encodedQuery}`;

        https.get(url, (res) => {
            let data = '';

            res.on('data', (chunk) => {
                data += chunk;
            });

            res.on('end', () => {
                try {
                    const result = JSON.parse(data);
                    resolve({
                        title: result.title || '',
                        extract: result.extract || '',
                        thumbnail: (result.thumbnail && result.thumbnail.source) || '',
                        url: (result.content_urls && result.content_urls.desktop && result.content_urls.desktop.page) || ''
                    });
                } catch (error) {
                    resolve({ title: '', extract: '', thumbnail: '', url: '' });
                }
            });
        }).on('error', (error) => {
            resolve({ title: '', extract: '', thumbnail: '', url: '' });
        });

        setTimeout(() => {
            resolve({ title: '', extract: '', thumbnail: '', url: '' });
        }, 5000);
    });
};

// Enhanced AI Response with Web Search
const generateAIResponse = async(message, conversationHistory = []) => {
    const lowerMessage = message.toLowerCase().trim();

    // Check if this is a knowledge/search question
    const searchKeywords = [
        'la gi', 'what is', 'who is', 'khi nao', 'when', 'o dau', 'where',
        'bao nhieu', 'how many', 'nhu the nao', 'how', 'tai sao', 'why',
        'giai thich', 'explain', 'tim kiem', 'search', 'tra cuu', 'lookup',
        'thong tin', 'information', 'lich su', 'history', 'dinh nghia', 'definition',
        'cong thuc', 'formula', 'tinh', 'calculate', 'code', 'lap trinh',
        'programming', 'javascript', 'python', 'java', 'react', 'nodejs',
        'thoi tiet', 'weather', 'tin tuc', 'news', 'gia', 'price'
    ];

    const needsSearch = searchKeywords.some(keyword => lowerMessage.includes(keyword)) ||
        lowerMessage.length > 20;

    let webResults = null;
    let wikiResult = null;
    let searchQuery = message;

    // Extract search query (remove common Vietnamese question starters)
    searchQuery = message
        .replace(/^(xin chao|chao|ban co the|ban hay|lam on|vui long)\s*/i, '')
        .replace(/^(what is|who is|when|where|how|why)\s*/i, '')
        .trim();

    // Perform parallel searches for relevant queries
    if (needsSearch && searchQuery.length > 3) {
        console.log('AI: Performing web search for:', searchQuery);

        try {
            [webResults, wikiResult] = await Promise.all([
                searchWeb(searchQuery),
                searchWikipedia(searchQuery.split(' ').slice(0, 3).join('_'))
            ]);
        } catch (error) {
            console.error('Search error:', error);
        }
    }

    // Generate response based on search results
    let response = '';
    let sources = [];

    if ((webResults && webResults.abstract) || (webResults && webResults.answer) || (wikiResult && wikiResult.extract)) {
        // Use web search results
        if (webResults && webResults.answer) {
            response = webResults.answer;
        } else if (webResults && webResults.abstract) {
            response = webResults.abstract;
        } else if (wikiResult && wikiResult.extract) {
            response = wikiResult.extract;
        }

        sources.push('DuckDuckGo');
        if (wikiResult && wikiResult.title) {
            sources.push('Wikipedia: ' + wikiResult.title);
        }

        // Add related information if available
        if (webResults && webResults.relatedTopics && webResults.relatedTopics.length > 0) {
            response = response + '\n\nThong tin lien quan:\n';
            webResults.relatedTopics.forEach(topic => {
                response = response + '- ' + topic + '\n';
            });
        }

        // Add Wikipedia citation
        if (wikiResult && wikiResult.url) {
            response = response + '\nNguon: Wikipedia - ' + wikiResult.title;
        }

    } else if (lowerMessage.includes('hello') || lowerMessage.includes('xin chao') || lowerMessage.includes('chao')) {
        response = 'Xin chao! Toi la AI assistant. Toi co the:\n' +
            '- Tra loi cau hoi cua ban\n' +
            '- Tim kiem thong tin tren internet\n' +
            '- Giai thich cac khai niem\n' +
            '- Ho tro lap trinh\n' +
            '- Va nhieu thu khac!\n\n' +
            'Ban can toi giup gi hom nay?';
    } else if (lowerMessage.includes('time') || lowerMessage.includes('thoi gian') || lowerMessage.includes('may gio')) {
        response = 'Hien tai la: ' + new Date().toLocaleString('vi-VN', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    } else if (lowerMessage.includes('weather') || lowerMessage.includes('thoi tiet')) {
        response = 'Toi chua kiem tra thoi tiet truc tiep, nhung ban co the:\n' +
            '- Xem ung dung thoi tiet tren dien thoai\n' +
            '- Truy cap accuweather.com\n' +
            '- Hoi toi ve khai niem thoi tiet!';
    } else if (lowerMessage.includes('code') || lowerMessage.includes('lap trinh') ||
        lowerMessage.includes('programming') || lowerMessage.includes('javascript') ||
        lowerMessage.includes('python') || lowerMessage.includes('java')) {
        response = 'Toi co the ho tro ban ve lap trinh!\n\n' +
            'Vui long cho toi biet:\n' +
            '- Ngon ngu lap trinh (JavaScript, Python, Java, etc.)\n' +
            '- Van de ban dang gap phai\n' +
            '- Code ban da viet (neu co)\n\n' +
            'Toi se co gang tim kiem giai thich cho ban!';
    } else {
        response = 'Cam on ban da hoi!\n\n' +
            'Toi da nhan duoc cau hoi cua ban.';

        if (needsSearch) {
            response = response + '\n\nToi dang tim kiem thong tin lien quan... ';

            if ((!webResults || !webResults.abstract) && (!wikiResult || !wikiResult.extract)) {
                response = response + '\n\nKhong tim duoc thong tin cu the. Ban co the:\n' +
                    '- Thu hoi voi tu khoa khac\n' +
                    '- Hoi cu the hon ve chu de\n' +
                    '- Hoi ve lich su, dia ly, khoa hoc, etc.';
            }
        }

        response = response + '\n\nBan co hoi them dieu gi khac khong?';
    }

    return {
        response: response,
        model: 'web-ai-v1',
        sources: sources.length > 0 ? sources : ['Local Knowledge'],
        tokens: {
            input: Math.ceil(message.length / 4),
            output: Math.ceil(response.length / 4)
        },
        metadata: {
            searched: needsSearch,
            webResults: !!(webResults && (webResults.abstract || webResults.answer)),
            wikiResult: !!(wikiResult && wikiResult.extract)
        }
    };
};

const sendMessage = async(req, res) => {
    console.log('--- SEND MESSAGE CALLED ---');
    console.log('User ID from token:', req.user.id);

    try {
        const { message, conversationId } = req.body;
        const userId = req.user.id;

        console.log('Request body:', { message, conversationId });
        console.log('User ID:', userId);

        if (!message || message.trim().length === 0) {
            console.log('Error: Message is empty');
            return res.status(400).json({ success: false, message: 'Tin nhan khong duoc de trong' });
        }

        // Validate userId
        if (!userId) {
            console.log('Error: User ID is missing');
            return res.status(401).json({ success: false, message: 'Nguoi dung chua duoc xac thuc' });
        }

        // Tao conversationId moi neu khong co
        const finalConversationId = conversationId || new mongoose.Types.ObjectId().toString();
        console.log('Using conversationId:', finalConversationId);

        const history = conversationId ?
            await AiChat.getConversationHistory(userId, conversationId, 10) : [];

        const startTime = Date.now();
        const aiResult = await generateAIResponse(message, history);
        const duration = Date.now() - startTime;

        const newChat = new AiChat({
            userId,
            conversationId: finalConversationId,
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
        console.log('Chat saved successfully, chatId:', newChat._id);

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
        console.error('Send message error:', error.message);
        console.error('Stack trace:', error.stack);
        res.status(500).json({
            success: false,
            message: 'Loi server khi gui tin nhan: ' + error.message
        });
    }
};

const getChatHistory = async(req, res) => {
    try {
        const { conversationId } = req.params;
        const { page = 1, limit = 20 } = req.query;
        const userId = req.user.id;
        const skip = (parseInt(page) - 1) * parseInt(limit);

        const chats = await AiChat.getConversationHistory(userId, conversationId, parseInt(limit), skip);
        const total = await AiChat.countDocuments({ userId, conversationId, isDeleted: false });

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
        res.status(500).json({ success: false, message: 'Loi lich su chat' });
    }
};

const getUserConversations = async(req, res) => {
    try {
        const userId = req.user.id;
        const conversations = await AiChat.getUserConversations(userId);
        res.json({ success: true, data: conversations });
    } catch (error) {
        res.status(500).json({ success: false, message: 'Loi danh sach chat' });
    }
};

const deleteConversation = async(req, res) => {
    try {
        const { conversationId } = req.params;
        const userId = req.user.id;
        await AiChat.updateMany({ userId, conversationId }, { isDeleted: true });
        res.json({ success: true, message: 'Da xoa' });
    } catch (error) {
        res.status(500).json({ success: false, message: 'Loi xoa chat' });
    }
};

const deleteMessage = async(req, res) => {
    try {
        const { chatId } = req.params;
        const userId = req.user.id;
        const chat = await AiChat.findOne({ _id: chatId, userId });
        if (!chat) return res.status(404).json({ success: false, message: 'Khong tim thay' });
        await chat.softDelete();
        res.json({ success: true, message: 'Da xoa' });
    } catch (error) {
        res.status(500).json({ success: false, message: 'Loi xoa tin nhan' });
    }
};

const getChatStats = async(req, res) => {
    try {
        const userId = req.user.id;
        const stats = await AiChat.aggregate([
            { $match: { userId: new mongoose.Types.ObjectId(userId), isDeleted: false } },
            {
                $group: {
                    _id: null,
                    totalMessages: { $sum: 1 },
                    totalConversations: { $addToSet: '$conversationId' },
                    totalTokens: { $sum: '$tokens.total' },
                    avgResponseTime: { $avg: '$metadata.duration' }
                }
            }
        ]);

        res.json({
            success: true,
            data: stats[0] || { totalMessages: 0, totalConversations: 0 }
        });

    } catch (error) {
        res.status(500).json({ success: false, message: 'Loi thong ke: ' + error.message });
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