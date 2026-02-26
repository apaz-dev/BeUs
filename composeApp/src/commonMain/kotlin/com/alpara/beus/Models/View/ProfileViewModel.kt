package com.alpara.beus.Models.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Firebase.Auth.FirebaseProfileService
import com.alpara.beus.Firebase.Auth.FirebaseAuthService
import com.alpara.beus.Models.ProfilePrivate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val firebaseProfileService = FirebaseProfileService()
    private val authService = FirebaseAuthService()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _updateError = MutableStateFlow<String?>(null)
    val updateError: StateFlow<String?> = _updateError.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            val userId = authService.getCurrentUserId()
            if (userId == null) {
                _profileState.value = ProfileState.Error("Usuario no autenticado")
                return@launch
            }

            firebaseProfileService.getProfile(userId).fold(
                onSuccess = { profile ->
                    _profileState.value = ProfileState.Success(profile)
                },
                onFailure = { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }

    fun updateProfile(newUsername: String, newPassword: String? = null) {
        viewModelScope.launch {
            _isUpdating.value = true
            _updateError.value = null

            val userId = authService.getCurrentUserId()
            if (userId == null) {
                _updateError.value = "Usuario no autenticado"
                _isUpdating.value = false
                return@launch
            }

            // Preparar actualizaciones
            val updates = mutableMapOf<String, Any>()

            // Actualizar username si cambió
            val currentProfile = (_profileState.value as? ProfileState.Success)?.profile
            if (currentProfile != null && newUsername != currentProfile.username) {
                updates["username"] = newUsername

                // Actualizar también el displayName en Firebase Auth
                authService.updateDisplayName(newUsername).fold(
                    onSuccess = { /* DisplayName actualizado correctamente */ },
                    onFailure = { error ->
                        _updateError.value = "Error al actualizar nombre: ${error.message}"
                        _isUpdating.value = false
                        return@launch
                    }
                )
            }

            // Actualizar contraseña si se proporcionó
            if (!newPassword.isNullOrEmpty()) {
                authService.updatePassword(newPassword).fold(
                    onSuccess = { /* Password actualizado correctamente */ },
                    onFailure = { error ->
                        _updateError.value = "Error al actualizar contraseña: ${error.message}"
                        _isUpdating.value = false
                        return@launch
                    }
                )
            }

            // Actualizar perfil en Firestore si hay cambios
            if (updates.isNotEmpty()) {
                firebaseProfileService.updateProfile(userId, updates).fold(
                    onSuccess = {
                        // Recargar perfil para mostrar cambios
                        loadProfile()
                    },
                    onFailure = { error ->
                        _updateError.value = error.message ?: "Error al actualizar perfil"
                    }
                )
            }

            _isUpdating.value = false
        }
    }

    fun deleteAccount(password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val userId = authService.getCurrentUserId()
            if (userId == null) {
                onError("Usuario no autenticado")
                return@launch
            }

            // Primero eliminar la cuenta de Firebase Auth (con re-autenticación)
            // Esto es lo más importante y lo que tiene permisos garantizados
            authService.deleteAccount(password).fold(
                onSuccess = {
                    // Intentar eliminar el perfil de Firestore
                    // Si falla no es crítico, la cuenta ya fue eliminada
                    viewModelScope.launch {
                        firebaseProfileService.deleteProfile(userId).fold(
                            onSuccess = {
                                // Perfil eliminado exitosamente
                                onSuccess()
                            },
                            onFailure = {
                                // Perfil no se pudo eliminar, pero la cuenta sí
                                // Continuar de todas formas
                                onSuccess()
                            }
                        )
                    }
                },
                onFailure = { error ->
                    onError(error.message ?: "Error al eliminar cuenta")
                }
            )
        }
    }
}

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Success(val profile: ProfilePrivate) : ProfileState()
    data class Error(val message: String) : ProfileState()
}