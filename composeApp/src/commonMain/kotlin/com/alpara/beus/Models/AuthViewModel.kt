package com.alpara.beus.Models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.data.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        checkAuthStatus()
    }

    /**
     * Verifica si hay una sesión activa al iniciar la app
     */
    fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val session = SupabaseClient.client.auth.currentSessionOrNull()
                _isAuthenticated.value = session != null
            } catch (e: Exception) {
                _isAuthenticated.value = false
            }
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    fun login(email: String, password: String, onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                _isAuthenticated.value = true
                _isLoading.value = false
                onLoginSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message ?: "Error al iniciar sesión"
                _isAuthenticated.value = false
            }
        }
    }

    /**
     * Registra un nuevo usuario con email, contraseña y nombre
     */
    fun signUp(email: String, password: String, nombre: String, onSignupSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    this.data = mapOf("nombre" to nombre)
                }
                
                _isAuthenticated.value = true
                _isLoading.value = false
                onSignupSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message ?: "Error al registrarse"
                _isAuthenticated.value = false
            }
        }
    }

    /**
     * Cierra la sesión del usuario
     */
    fun logout() {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
                _isAuthenticated.value = false
            } catch (e: Exception) {
                // Aún así cerramos la sesión localmente
                _isAuthenticated.value = false
            }
        }
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}