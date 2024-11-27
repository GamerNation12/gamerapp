package com.example.gamerapp.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChatMessage(
    val id: String = "",
    val content: String = "",
    val sender: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val platform: String = "",
    val deleted: Boolean = false
) {
    // Required empty constructor for Firebase
    constructor() : this("", "", "", 0L, "", false)
} 