package com.alpara.beus.Models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Network.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authService = AuthService()
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

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            authService.register(username, email, password)
                .onSuccess { response ->
                    _isAuthenticated.value = true
                }
                .onFailure { error ->
                    _authError.value = error.message ?: "Error al iniciar sesión"
                }
            _isLoading.value = false
        }
    }

    fun logout() {
        _isAuthenticated.value = false
    }
    fun checkAuthStatus() {
        // Verificar token guardado
    }
}