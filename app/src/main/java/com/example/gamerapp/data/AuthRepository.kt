package com.example.gamerapp.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    suspend fun signIn(username: String): String? {
        return try {
            auth.signInAnonymously().await()
            auth.currentUser?.uid
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentUser(): String? {
        return auth.currentUser?.uid
    }

    fun signOut() {
        auth.signOut()
    }
} 