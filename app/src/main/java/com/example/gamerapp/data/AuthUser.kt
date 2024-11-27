package com.example.gamerapp.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthUser(
    val id: String,
    val username: String,
    val email: String,
    val isAdmin: Boolean = false
) 