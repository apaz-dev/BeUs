package com.alpara.beus.Models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Network.AuthService
import com.alpara.beus.Security.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val tokenManager: TokenManager) : ViewModel() {
    private val authService = AuthService(tokenManager)
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            authService.login(email, password)
                .onSuccess { response ->
                    _isAuthenticated.value = true
                }
                .onFailure { error ->
                    _authError.value = error.message ?: "Error al iniciar sesión"
                }
            _isLoading.value = false
        }
    }

    private val _isRegister = MutableStateFlow(false)
    val isRegister = _isRegister.asStateFlow()

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            authService.register(username, email, password)
                .onSuccess { response ->
                    _isRegister.value = true
                }
                .onFailure { error ->
                    _authError.value = error.message ?: "Error al iniciar sesión"
                }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearTokens()
            _isAuthenticated.value = false
        }
    }
    fun checkAuthStatus() {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken()
            _isAuthenticated.value = !token.isNullOrEmpty()
        }
    }
}