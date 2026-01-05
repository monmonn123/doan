# Android App Backend (Node.js + Express + Mongoose)

## Tech Stack
- **Framework:** Express.js
- **Database:** MongoDB Atlas (Mongoose)
- **Authentication:** JWT, Bcrypt
- **Logging:** Morgan
- **CORS:** Enabled

## Setup Instructions

1.  **Install dependencies:**
    ```bash
    cd server
    npm install
    ```

2.  **Environment Variables:**
    - Copy `.env.example` to `.env`
    - Update `MONGO_URI` with your MongoDB Atlas connection string.
    - Set a `JWT_SECRET`.

3.  **Run the server:**
    - Development mode: `npm run dev`
    - Production mode: `npm start`

## API Endpoints
- `GET /health`: Check if server is running.
- Base URL: `http://<your-ip>:5000/api` (Note: Use your machine IP instead of localhost when connecting from Android emulator/device).
