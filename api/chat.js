const admin = require('firebase-admin');
const serviceAccount = require('../firebase-service-account.json');

// Initialize Firebase Admin
if (!admin.apps.length) {
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
        databaseURL: "https://gamer-app-10a85-default-rtdb.firebaseio.com"
    });
}

export default async function handler(req, res) {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

    if (req.method === 'POST') {
        try {
            const { action, data } = req.body;

            switch (action) {
                case 'sendMessage':
                    // Check message content
                    if (containsBadWords(data.content)) {
                        return res.json({ error: 'Message contains inappropriate content' });
                    }
                    
                    // Add message to database
                    const messageRef = admin.database().ref('messages').push();
                    await messageRef.set({
                        ...data,
                        id: messageRef.key,
                        timestamp: Date.now()
                    });
                    return res.json({ success: true });

                case 'banUser':
                    // Only admins can ban users
                    if (!data.adminKey || !verifyAdmin(data.adminKey)) {
                        return res.status(403).json({ error: 'Unauthorized' });
                    }
                    
                    await admin.database().ref(`bannedUsers/${data.userId}`).set({
                        reason: data.reason,
                        bannedBy: data.adminId,
                        bannedAt: Date.now()
                    });
                    return res.json({ success: true });

                default:
                    return res.status(400).json({ error: 'Invalid action' });
            }
        } catch (error) {
            return res.status(500).json({ error: error.message });
        }
    }

    res.status(405).json({ error: 'Method not allowed' });
}

function containsBadWords(text) {
    const badWords = ['badword1', 'badword2']; // Add your list of bad words
    return badWords.some(word => text.toLowerCase().includes(word));
}

function verifyAdmin(adminKey) {
    // Add your admin verification logic
    return adminKey === process.env.ADMIN_KEY;
} 