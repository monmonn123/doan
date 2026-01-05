# AI Chat API Documentation

## Tổng quan
Hệ thống chat với AI bao gồm model lưu trữ tin nhắn và các API endpoints để tương tác với AI assistant.

## Model: AiChat.js
Model lưu trữ thông tin tin nhắn chat với AI, bao gồm:
- **userId**: ID của user gửi tin nhắn
- **conversationId**: ID của cuộc trò chuyện
- **message**: Nội dung tin nhắn của user
- **response**: Phản hồi từ AI
- **messageType**: Loại tin nhắn ('user', 'assistant')
- **model**: Tên model AI được sử dụng
- **tokens**: Thống kê token (input, output, total)
- **metadata**: Thông tin bổ sung (duration, temperature, maxTokens)
- **timestamps**: createdAt, updatedAt

## API Endpoints

### Authentication
Tất cả API endpoints đều yêu cầu JWT token trong header:
```
Authorization: Bearer <your_jwt_token>
```

### 1. Gửi tin nhắn và nhận phản hồi từ AI
**POST** `/api/chat/send`

**Request Body:**
```json
{
    "message": "Xin chào AI",
    "conversationId": "optional_conversation_id" // Nếu không có sẽ tạo mới
}
```

**Response:**
```json
{
    "success": true,
    "data": {
        "chatId": "665f1a2b3c4d5e6f7890abcd",
        "conversationId": "665f1a2b3c4d5e6f7890abcd",
        "message": "Xin chào AI",
        "response": "Xin chào! Tôi là AI assistant...",
        "timestamp": "2024-06-14T10:30:00.000Z",
        "model": "mock-ai-v1",
        "tokens": {
            "input": 10,
            "output": 25,
            "total": 35
        },
        "metadata": {
            "duration": 1500,
            "temperature": 0.7,
            "maxTokens": 1000
        }
    }
}
```

### 2. Lấy lịch sử chat
**GET** `/api/chat/history/:conversationId`

**Query Parameters:**
- `page` (optional): Số trang, mặc định là 1
- `limit` (optional): Số tin nhắn mỗi trang, mặc định là 20

**Response:**
```json
{
    "success": true,
    "data": {
        "chats": [
            {
                "_id": "665f1a2b3c4d5e6f7890abcd",
                "userId": "665f1a2b3c4d5e6f7890abcd",
                "conversationId": "665f1a2b3c4d5e6f7890abcd",
                "message": "Xin chào AI",
                "response": "Xin chào! Tôi là AI assistant...",
                "messageType": "user",
                "model": "mock-ai-v1",
                "tokens": {
                    "input": 10,
                    "output": 25,
                    "total": 35
                },
                "metadata": {
                    "duration": 1500,
                    "temperature": 0.7,
                    "maxTokens": 1000
                },
                "createdAt": "2024-06-14T10:30:00.000Z",
                "updatedAt": "2024-06-14T10:30:00.000Z"
            }
        ],
        "pagination": {
            "currentPage": 1,
            "totalPages": 1,
            "totalItems": 1,
            "itemsPerPage": 20
        }
    }
}
```

### 3. Lấy danh sách cuộc trò chuyện
**GET** `/api/chat/conversations`

**Response:**
```json
{
    "success": true,
    "data": [
        {
            "_id": "665f1a2b3c4d5e6f7890abcd",
            "lastMessage": "Hôm nay thời tiết thế nào?",
            "lastResponse": "Tôi không thể kiểm tra thời tiết trực tiếp...",
            "messageCount": 5,
            "firstMessageAt": "2024-06-14T09:00:00.000Z",
            "lastMessageAt": "2024-06-14T10:30:00.000Z",
            "model": "mock-ai-v1"
        }
    ]
}
```

### 4. Xóa cuộc trò chuyện
**DELETE** `/api/chat/conversation/:conversationId`

**Response:**
```json
{
    "success": true,
    "message": "Đã xóa cuộc trò chuyện thành công"
}
```

### 5. Xóa tin nhắn cụ thể
**DELETE** `/api/chat/message/:chatId`

**Response:**
```json
{
    "success": true,
    "message": "Đã xóa tin nhắn thành công"
}
```

### 6. Thống kê chat của user
**GET** `/api/chat/stats`

**Response:**
```json
{
    "success": true,
    "data": {
        "totalMessages": 150,
        "totalConversations": 5,
        "totalTokens": 3750,
        "avgResponseTime": 1200.5
    }
}
```

## Cách sử dụng

### 1. Đăng nhập để lấy JWT token
```bash
curl -X POST http://localhost:4000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### 2. Gửi tin nhắn chat đầu tiên
```bash
curl -X POST http://localhost:4000/api/chat/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "message": "Xin chào AI"
  }'
```

### 3. Tiếp tục cuộc trò chuyện
```bash
curl -X POST http://localhost:4000/api/chat/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "message": "Hôm nay thời tiết thế nào?",
    "conversationId": "665f1a2b3c4d5e6f7890abcd"
  }'
```

### 4. Xem lịch sử cuộc trò chuyện
```bash
curl -X GET http://localhost:4000/api/chat/history/665f1a2b3c4d5e6f7890abcd \
  -H "Authorization: Bearer <your_jwt_token>"
```

### 5. Xem danh sách tất cả cuộc trò chuyện
```bash
curl -X GET http://localhost:4000/api/chat/conversations \
  -H "Authorization: Bearer <your_jwt_token>"
```

## Tính năng đặc biệt

### Mock AI Responses
Hiện tại hệ thống sử dụng mock AI responses với các từ khóa:
- "hello", "hi" → Phản hồi chào hỏi
- "help" → Hướng dẫn sử dụng
- "time" → Hiển thị thời gian hiện tại
- "weather" → Thông báo không thể kiểm tra thời tiết
- "code" → Hỗ trợ lập trình
- Các từ khóa khác → Phản hồi mặc định

### Tính năng mở rộng
- Có thể thay thế mock AI bằng OpenAI API, Claude API, hoặc các AI service khác
- Hỗ trợ soft delete cho tin nhắn
- Index tối ưu cho performance
- Thống kê chi tiết về usage
- Phân trang cho lịch sử chat

## Cài đặt và chạy

1. Đảm bảo MongoDB đang chạy
2. Cài đặt dependencies: `npm install`
3. Chạy server: `npm start`
4. Server sẽ chạy trên port 4000

## Environment Variables
- `MONGO_URI`: MongoDB connection string
- `JWT_SECRET`: Secret key cho JWT token
- `PORT`: Port server (mặc định 4000)
