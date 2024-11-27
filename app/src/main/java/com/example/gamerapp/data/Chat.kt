package com.example.gamerapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: String = "",
    val message: String = "",
    val sender: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) 