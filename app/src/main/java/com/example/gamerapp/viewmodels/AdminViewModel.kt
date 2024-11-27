package com.example.gamerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamerapp.data.User
import com.example.gamerapp.data.UserRepository
import com.example.gamerapp.data.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun toggleUserActive(userId: String, currentlyActive: Boolean) {
        viewModelScope.launch {
            userRepository.updateUser(userId, !currentlyActive)
        }
    }

    fun updateUserRole(userId: String, newRole: UserRole) {
        viewModelScope.launch {
            userRepository.updateUserRole(userId, newRole)
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            userRepository.updateUser(userId, false) // Soft delete by deactivating
        }
    }
} 