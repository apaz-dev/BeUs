package com.alpara.beus.Models.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Firebase.Auth.FirebaseAuthService
import com.alpara.beus.Firebase.Auth.FirebaseTeamService
import com.alpara.beus.Models.TeamDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TeamDetailState {
    data object Loading : TeamDetailState()
    data class Success(val detail: TeamDetail) : TeamDetailState()
    data class Error(val message: String) : TeamDetailState()
}

class TeamDetailViewModel : ViewModel() {

    private val teamService = FirebaseTeamService()
    private val authService = FirebaseAuthService()

    private val _state = MutableStateFlow<TeamDetailState>(TeamDetailState.Loading)
    val state: StateFlow<TeamDetailState> = _state.asStateFlow()

    private val _actionResult = MutableStateFlow<String?>(null)
    val actionResult: StateFlow<String?> = _actionResult.asStateFlow()

    fun loadTeamDetail(teamId: String) {
        viewModelScope.launch {
            _state.value = TeamDetailState.Loading
            val userId = authService.getCurrentUserId()
            if (userId == null) {
                _state.value = TeamDetailState.Error("Usuario no autenticado")
                return@launch
            }
            teamService.getTeamDetail(userId, teamId).fold(
                onSuccess = { _state.value = TeamDetailState.Success(it) },
                onFailure = { _state.value = TeamDetailState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    fun kickMember(targetUserId: String, onDone: () -> Unit) {
        val current = _state.value as? TeamDetailState.Success ?: return
        val detail = current.detail
        viewModelScope.launch {
            teamService.kickMember(
                teamId = detail.teamId,
                targetUserId = targetUserId,
                teamCode = detail.joinCode,
                teamName = detail.name
            ).fold(
                onSuccess = {
                    _actionResult.value = "Miembro expulsado"
                    loadTeamDetail(detail.teamId)
                    onDone()
                },
                onFailure = { _actionResult.value = "Error: ${it.message}" }
            )
        }
    }

    fun leaveTeam(onDone: () -> Unit) {
        val current = _state.value as? TeamDetailState.Success ?: return
        val detail = current.detail
        viewModelScope.launch {
            teamService.leaveTeam(
                currentUserId = detail.currentUserId,
                teamId = detail.teamId,
                teamCode = detail.joinCode
            ).fold(
                onSuccess = {
                    _actionResult.value = "Has salido del equipo"
                    onDone()
                },
                onFailure = { _actionResult.value = "Error: ${it.message}" }
            )
        }
    }

    fun dissolveTeam(onDone: () -> Unit) {
        val current = _state.value as? TeamDetailState.Success ?: return
        val detail = current.detail
        viewModelScope.launch {
            teamService.dissolveTeam(
                teamId = detail.teamId,
                teamCode = detail.joinCode
            ).fold(
                onSuccess = {
                    _actionResult.value = "Equipo disuelto"
                    onDone()
                },
                onFailure = { _actionResult.value = "Error: ${it.message}" }
            )
        }
    }

    fun clearActionResult() {
        _actionResult.value = null
    }
}
