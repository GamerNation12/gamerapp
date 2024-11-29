import React, { useState, useEffect } from 'react';
import io from 'socket.io-client';

const socket = io('https://gamer-app-backend.onrender.com');

function Chat() {
  const [messages, setMessages] = useState([]);
  const [messageText, setMessageText] = useState('');

  useEffect(() => {
    fetchMessages();
    
    socket.on('newMessage', (message) => {
      setMessages(prevMessages => [...prevMessages, message]);
    });

    return () => socket.off('newMessage');
  }, []);

  const fetchMessages = async () => {
    try {
      const response = await fetch('https://gamer-app-backend.onrender.com/api/messages');
      if (response.ok) {
        const data = await response.json();
        setMessages(data);
      }
    } catch (error) {
      console.error('Error fetching messages:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!messageText.trim()) return;

    try {
      const response = await fetch('https://gamer-app-backend.onrender.com/api/messages', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          message: messageText.trim()  // Changed from text to message
        })
      });

      if (!response.ok) {
        throw new Error('Failed to send message');
      }

      setMessageText('');
    } catch (error) {
      console.error('Error sending message:', error);
    }
  };

  return (
    <div className="chat-container">
      <div className="messages">
        {messages.map((message, index) => (
          <div key={index} className="message">
            <strong>{message.userId || 'User'}</strong>
            <p>{message.text || message.message}</p>
          </div>
        ))}
      </div>
      <form onSubmit={handleSubmit} className="message-form">
        <input
          type="text"
          value={messageText}
          onChange={(e) => setMessageText(e.target.value)}
          placeholder="Type your message..."
        />
        <button type="submit">Send</button>
      </form>
    </div>
  );
}

export default Chat; 