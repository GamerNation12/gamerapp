package com.example.gamerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gamerapp.data.ChatRepository

class ChatViewModelFactory(
    private val chatRepository: ChatRepository,
    private val username: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRepository, username) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 