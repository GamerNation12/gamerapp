package com.example.gamerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamerapp.data.AuthRepository
import com.example.gamerapp.data.User
import com.example.gamerapp.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun login(username: String) {
        viewModelScope.launch {
            val userId = authRepository.signIn(username)
            if (userId != null) {
                val user = userRepository.createUser(
                    email = "", // Anonymous login doesn't have email
                    username = username
                )
                _currentUser.value = user
                _isLoggedIn.value = true
            }
        }
    }

    fun logout() {
        authRepository.signOut()
        _currentUser.value = null
        _isLoggedIn.value = false
    }
} 