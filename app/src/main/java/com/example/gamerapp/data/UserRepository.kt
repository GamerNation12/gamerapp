package com.example.gamerapp.data

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val database = FirebaseDatabase.getInstance().getReference("users")

    suspend fun createUser(email: String, username: String): User? {
        return try {
            val userRef = database.push()
            val user = User(
                id = userRef.key ?: "",
                email = email,
                username = username
            )
            userRef.setValue(user).await()
            user
        } catch (e: Exception) {
            Log.e("UserRepository", "Error creating user", e)
            null
        }
    }

    suspend fun updateUser(userId: String, isActive: Boolean) {
        try {
            database.child(userId).child("isActive").setValue(isActive).await()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user", e)
        }
    }

    suspend fun updateUserRole(userId: String, role: UserRole) {
        try {
            database.child(userId).child("role").setValue(role).await()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user role", e)
        }
    }

    suspend fun getUser(userId: String): User? {
        return try {
            val snapshot = database.child(userId).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user", e)
            null
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return try {
            val snapshot = database.orderByChild("email").equalTo(email).get().await()
            snapshot.children.firstOrNull()?.getValue(User::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user by email", e)
            null
        }
    }
} 