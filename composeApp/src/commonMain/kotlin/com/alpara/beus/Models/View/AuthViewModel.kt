package com.alpara.beus.Models.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Firebase.Auth.FirebaseAuthService
import com.alpara.beus.Firebase.Auth.FirebaseProfileService
import com.alpara.beus.Security.TokenManager
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val tokenManager: TokenManager) : ViewModel() {
    private val authService = FirebaseAuthService()
    private val firebaseProfileService = FirebaseProfileService()
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isCheckingAuth = MutableStateFlow(true)
    val isCheckingAuth: StateFlow<Boolean> = _isCheckingAuth.asStateFlow()
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            authService.login(email, password)
                .onSuccess { response ->
                    // Save tokens
                    tokenManager.saveAccessToken(response.accessToken)
                    tokenManager.saveRefreshToken(response.refreshToken)
                    _isAuthenticated.value = true
                }
                .onFailure { error ->
                    _authError.value = error.message ?: "Error al iniciar sesión"
                    _isAuthenticated.value = false

                }
            _isLoading.value = false
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            authService.register(username, email, password)
                .onSuccess {
                    // Lanzar createProfile y getToken en paralelo para no esperar uno tras otro
                    coroutineScope {
                        val userId = authService.getCurrentUserId()
                        val profileJob = async {
                            if (userId != null) firebaseProfileService.createProfile(userId, username, email)
                        }
                        val tokenJob = async {
                            authService.getCurrentUserToken() ?: ""
                        }
                        val token = tokenJob.await()
                        profileJob.await()
                        tokenManager.saveAccessToken(token)
                        tokenManager.saveRefreshToken(userId ?: "")
                    }
                    _isAuthenticated.value = true
                }
                .onFailure { error ->
                    _authError.value = error.message ?: "Error al registrarse"
                }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            authService.logout()
            tokenManager.clearTokens()
            _isAuthenticated.value = false
        }
    }
    fun checkAuthStatus() {
        viewModelScope.launch {
            _isCheckingAuth.value = true
            _isAuthenticated.value = authService.checkAuthStatus()
            _isCheckingAuth.value = false
        }
    }
}