package com.alpara.beus

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthState {
    var isLoggedIn by mutableStateOf(false)
        private set

    fun login() {
        isLoggedIn = true
    }

    fun logout() {
        isLoggedIn = false
    }
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow(false)
    val isAuthenticated = _authState.asStateFlow()

    fun checkAuthStatus() {
        // Verificar token guardado
    }

    fun login() {
        _authState.value = true
    }

    fun logout() {
        _authState.value = false
    }
}
