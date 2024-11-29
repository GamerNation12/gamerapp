// Location: gamer-app-backend/src/routes/messages.js
// API routes for handling messages
// Handles:
// - GET /api/messages: Retrieves all messages from Firebase
// - POST /api/messages: Saves new messages to Firebase
// - Message validation and error handling

const express = require('express');
const router = express.Router();
const admin = require('../config/firebase');

router.get('/', async (req, res) => {
  try {
    if (!admin.apps.length) {
      return res.json([]);
    }

    const messagesRef = admin.database().ref('messages');
    const snapshot = await messagesRef.once('value');
    const messages = snapshot.val() || {};
    
    res.json(Object.values(messages));
  } catch (error) {
    console.error('Error fetching messages:', error);
    res.status(500).json({ 
      success: false, 
      error: error.message 
    });
  }
});

router.post('/', async (req, res) => {
  try {
    console.log('Received message:', req.body);

    // Validate the incoming message
    if (!req.body || !req.body.content) {
      console.log('Message content is empty');
      return res.status(400).json({
        success: false,
        error: 'Message content is required'
      });
    }

    // Save to Firebase if initialized
    if (admin.apps.length) {
      console.log('Firebase is initialized, saving message...');
      const messagesRef = admin.database().ref('messages');
      try {
        await messagesRef.child(req.body.id).set(req.body);
        console.log('Message saved successfully with id:', req.body.id);
        return res.status(200).json({ 
          success: true,
          messageId: req.body.id
        });
      } catch (dbError) {
        console.error('Database operation error:', dbError);
        throw dbError;
      }
    } else {
      console.log('Firebase is not initialized');
      return res.status(200).json({ 
        success: true,
        message: 'Message received (Firebase not initialized)'
      });
    }
  } catch (error) {
    console.error('Error saving message:', error);
    res.status(500).json({ 
      success: false, 
      error: error.message 
    });
  }
});

module.exports = router; 