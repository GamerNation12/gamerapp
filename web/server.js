require('dotenv').config();
const express = require('express');
const cors = require('cors');
const http = require('http');
const admin = require('firebase-admin');

const app = express();
const server = http.createServer(app);
const io = require('socket.io')(server, {
    cors: {
        origin: ["https://gamer-app-10a85.web.app", "http://localhost:3000"],
        methods: ["GET", "POST"],
        credentials: true
    }
});

// In-memory storage
const messages = [];
const bannedUsers = new Map();
let maintenanceMode = { enabled: false };

// Add admin credentials
const ADMIN_USERNAME = 'MGN';
const ADMIN_PASSWORD = 'MGN'; // In production, use hashed passwords

console.log('Starting server...');

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static('public'));

// Logging middleware
app.use((req, res, next) => {
    console.log(`${req.method} ${req.url}`);
    next();
});

// API Router
const apiRouter = express.Router();

// Login endpoint with admin check
apiRouter.post('/login', (req, res) => {
    const { username, password } = req.body;
    
    // Check if admin login
    if (username === ADMIN_USERNAME && password === ADMIN_PASSWORD) {
        return res.json({ success: true, username, isAdmin: true });
    }
    
    // Regular user login
    if (bannedUsers.has(username)) {
        return res.status(403).json(bannedUsers.get(username));
    }
    res.json({ success: true, username, isAdmin: false });
});

// Admin endpoints
apiRouter.post('/admin/clear-messages', (req, res) => {
    const { username } = req.body;
    if (username === ADMIN_USERNAME) {
        messages.length = 0;
        io.emit('messagesCleared');
        res.json({ success: true });
    } else {
        res.status(403).json({ error: 'Unauthorized' });
    }
});

apiRouter.post('/admin/maintenance', (req, res) => {
    const { username, enabled, message } = req.body;
    if (username === ADMIN_USERNAME) {
        maintenanceMode = { enabled, message, updatedAt: Date.now() };
        io.emit('maintenanceUpdated', maintenanceMode);
        res.json({ success: true });
    } else {
        res.status(403).json({ error: 'Unauthorized' });
    }
});

// Keep existing endpoints...
apiRouter.get('/messages', async (req, res) => {
    try {
        const snapshot = await admin.database().ref('messages').once('value');
        res.json(snapshot.val() || {});
    } catch (error) {
        res.status(500).json({ error: 'Failed to get messages' });
    }
});

apiRouter.post('/messages', async (req, res) => {
    try {
        const message = req.body;
        await admin.database().ref('messages').child(message.id).set(message);
        io.emit('newMessage', message);
        res.json({ success: true });
    } catch (error) {
        res.status(500).json({ error: 'Failed to send message' });
    }
});

// Ban/Unban endpoints (admin only)
apiRouter.post('/users/ban', (req, res) => {
    const { username, reason, bannedBy } = req.body;
    if (bannedBy === ADMIN_USERNAME) {
        bannedUsers.set(username, { reason, bannedBy, bannedAt: Date.now() });
        io.emit('userBanned', { username, reason, bannedBy });
        res.json({ success: true });
    } else {
        res.status(403).json({ error: 'Unauthorized' });
    }
});

apiRouter.delete('/users/ban/:username', (req, res) => {
    const { adminUsername } = req.body;
    if (adminUsername === ADMIN_USERNAME) {
        bannedUsers.delete(req.params.username);
        io.emit('userUnbanned', req.params.username);
        res.json({ success: true });
    } else {
        res.status(403).json({ error: 'Unauthorized' });
    }
});

// Mount API routes
app.use('/api', apiRouter);

// Socket.IO connection handling
io.on('connection', (socket) => {
    console.log('User connected');
    
    socket.on('disconnect', () => {
        console.log('User disconnected');
    });
});

// Error handling middleware
app.use((err, req, res, next) => {
    console.error('Error:', err.stack);
    res.status(500).json({ error: 'Something broke!' });
});

// Start server
const PORT = process.env.PORT || 3001;
server.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
    console.log(`API available at http://localhost:${PORT}/api`);
}); 