package com.alpara.beus.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.network.AuthService
import com.alpara.beus.models.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authService = AuthService()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

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

    fun register(email: String, username: String, password: String, fullName: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            val request = RegisterRequest(email, username, password, fullName)
            authService.register(request)
                .onSuccess {
                    login(email, password)
                }
                .onFailure { error ->
                    _authError.value = error.message ?: "Error al registrarse"
                }

            _isLoading.value = false
        }
    }
    fun logout() {
        _isAuthenticated.value = false
    }
}
