package com.example.gamerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamerapp.data.ChatMessage
import com.example.gamerapp.data.ChatRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val username: String
) : ViewModel() {

    private var lastMessageTime = 0L
    private val cooldownMs = 1000L // 1 second cooldown

    val messages: StateFlow<List<ChatMessage>> = chatRepository.messages

    fun sendMessage(content: String): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastMessageTime < cooldownMs) {
            return false // Still in cooldown
        }

        viewModelScope.launch {
            lastMessageTime = now
            chatRepository.sendMessage(content, username)
        }
        return true
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            chatRepository.deleteMessage(messageId)
        }
    }
} 