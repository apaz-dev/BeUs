package com.alpara.beus.Models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    fun login() {
        _isAuthenticated.value = true
    }

    fun logout() {
        _isAuthenticated.value = false
    }
    fun checkAuthStatus() {
        // Verificar token guardado
    }
}