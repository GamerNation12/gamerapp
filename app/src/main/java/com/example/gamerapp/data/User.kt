package com.example.gamerapp.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val role: UserRole = UserRole.USER,
    val isActive: Boolean = true
) 