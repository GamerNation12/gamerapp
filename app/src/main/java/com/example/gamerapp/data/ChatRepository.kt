package com.example.gamerapp.data

import android.util.Log
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val messagesRef = database.child("messages")
    private val maintenanceRef = database.child("maintenance")
    private val bannedUsersRef = database.child("bannedUsers")
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages
    
    private val _isInMaintenance = MutableStateFlow(false)
    val isInMaintenance: StateFlow<Boolean> = _isInMaintenance

    // Admin functions
    fun clearAllMessages() {
        messagesRef.removeValue()
    }

    fun banUser(username: String, reason: String, bannedBy: String) {
        bannedUsersRef.child(username).setValue(
            mapOf(
                "reason" to reason,
                "bannedBy" to bannedBy,
                "bannedAt" to ServerValue.TIMESTAMP
            )
        )
    }

    fun unbanUser(username: String) {
        bannedUsersRef.child(username).removeValue()
    }

    fun toggleMaintenance(enabled: Boolean, message: String, updatedBy: String) {
        maintenanceRef.setValue(
            mapOf(
                "enabled" to enabled,
                "message" to message,
                "updatedAt" to ServerValue.TIMESTAMP,
                "updatedBy" to updatedBy
            )
        )
    }

    init {
        setupMessageListener()
        setupMaintenanceListener()
    }

    private fun setupMaintenanceListener() {
        maintenanceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val maintenance = snapshot.value as? Map<*, *>
                _isInMaintenance.value = maintenance?.get("enabled") as? Boolean ?: false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatRepository", "Maintenance check failed", error.toException())
            }
        })
    }

    private fun setupMessageListener() {
        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val message = snapshot.getValue(ChatMessage::class.java)
                    if (message != null && !message.deleted) {
                        val currentMessages = _messages.value.toMutableList()
                        currentMessages.add(message.copy(id = snapshot.key ?: ""))
                        _messages.value = currentMessages.sortedBy { it.timestamp }
                    }
                } catch (e: Exception) {
                    Log.e("ChatRepository", "Error adding message", e)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val message = snapshot.getValue(ChatMessage::class.java)
                    if (message != null) {
                        val currentMessages = _messages.value.toMutableList()
                        val index = currentMessages.indexOfFirst { it.id == snapshot.key }
                        if (index != -1) {
                            if (message.deleted) {
                                currentMessages.removeAt(index)
                            } else {
                                currentMessages[index] = message.copy(id = snapshot.key ?: "")
                            }
                            _messages.value = currentMessages.sortedBy { it.timestamp }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ChatRepository", "Error updating message", e)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val currentMessages = _messages.value.toMutableList()
                currentMessages.removeIf { it.id == snapshot.key }
                _messages.value = currentMessages
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatRepository", "Database error: ${error.message}")
            }
        })
    }

    fun sendMessage(content: String, sender: String) {
        try {
            val messageRef = messagesRef.push()
            val message = ChatMessage(
                id = messageRef.key ?: "",
                content = content,
                sender = sender,
                timestamp = System.currentTimeMillis(),
                platform = "Mobile",
                deleted = false
            )
            messageRef.setValue(message)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error sending message", e)
        }
    }

    fun deleteMessage(messageId: String) {
        try {
            messagesRef.child(messageId).child("deleted").setValue(true)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error deleting message", e)
        }
    }
} 