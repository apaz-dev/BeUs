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
}

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Success(val profile: ProfilePrivate) : ProfileState()
    data class Error(val message: String) : ProfileState()
}