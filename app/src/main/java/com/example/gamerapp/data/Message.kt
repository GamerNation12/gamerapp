package com.example.gamerapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String = "",
    val content: String = "",
    val sender: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) 