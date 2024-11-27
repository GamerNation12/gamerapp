package com.example.gamerapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Admin(
    val id: String = "",
    val username: String = "",
    val permissions: List<String> = emptyList()
) 