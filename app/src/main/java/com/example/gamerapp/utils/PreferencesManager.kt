package com.example.gamerapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.gamerapp.data.ChatMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun saveMessages(messages: List<ChatMessage>) {
        val messagesJson = json.encodeToString(messages)
        prefs.edit().putString("messages", messagesJson).apply()
    }

    fun getMessages(): List<ChatMessage> {
        val messagesJson = prefs.getString("messages", "[]")
        return try {
            json.decodeFromString(messagesJson!!)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addMessage(message: ChatMessage) {
        val currentMessages = getMessages().toMutableList()
        currentMessages.add(message)
        saveMessages(currentMessages)
    }

    fun clearMessages() {
        prefs.edit().remove("messages").apply()
    }

    fun saveUserData(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getUserData(key: String): String? {
        return prefs.getString(key, null)
    }

    fun removeUserData(key: String) {
        prefs.edit().remove(key).apply()
    }
} 