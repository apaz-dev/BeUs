package com.alpara.beus.Models.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Firebase.Auth.FirebaseAuthService
import com.alpara.beus.Firebase.Auth.FirebaseTeamService
import com.alpara.beus.Models.TeamCreateResponse
import com.alpara.beus.Models.TeamJoinResponse
import kotlinx.coroutines.launch

class TeamViewModel : ViewModel() {

    private val teamService = FirebaseTeamService()
    private val authService = FirebaseAuthService()


    fun createTeam(teamName: String, onResult: (Result<TeamCreateResponse>) -> Unit) {
        viewModelScope.launch {
            val userId = authService.getCurrentUserId()
            if (userId == null) {
                onResult(Result.failure(Exception("Usuario no autenticado")))
                return@launch
            }
            teamService.createTeam(userId, teamName).fold(
                onSuccess = { res ->
                    onResult(Result.success(res))
                },
                onFailure = { err ->
                    onResult(Result.failure(Exception(err.message ?: "Error desconocido")))
                }
            )
        }
    }

    fun joinTeam(joinCode: String, onResult: (Result<TeamJoinResponse>) -> Unit) {
        viewModelScope.launch {
            val userId = authService.getCurrentUserId()
            if (userId == null) {
                onResult(Result.failure(Exception("Usuario no autenticado")))
                return@launch
            }
            teamService.joinTeamByCode(userId, joinCode).fold(
                onSuccess = { res ->
                    onResult(Result.success(res))
                },
                onFailure = { err ->
                    onResult(Result.failure(Exception(err.message ?: "Error desconocido")))
                }
            )
        }
    }
}