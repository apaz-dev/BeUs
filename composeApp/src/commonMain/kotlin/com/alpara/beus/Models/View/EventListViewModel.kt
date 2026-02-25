package com.alpara.beus.Models.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Firebase.Auth.FirebaseAuthService
import com.alpara.beus.Firebase.Auth.FirebaseEventService
import com.alpara.beus.Firebase.Auth.FirebaseTeamService
import com.alpara.beus.Models.EventData
import com.alpara.beus.Utils.EventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventListUiState(
    val events: List<EventData> = emptyList(),
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class EventListViewModel : ViewModel() {

    private val authService = FirebaseAuthService()
    private val eventService = FirebaseEventService()
    private val teamService = FirebaseTeamService()

    private val _uiState = MutableStateFlow(EventListUiState())
    val uiState: StateFlow<EventListUiState> = _uiState.asStateFlow()

    // Cache del teamId real una vez resuelto
    private var resolvedTeamId: String = ""

    fun loadEvents(codeOrId: String) {
        if (codeOrId.isBlank()) {
            _uiState.value = _uiState.value.copy(isLoading = false, events = emptyList())
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Resolver el ID real si aún no lo tenemos
            if (resolvedTeamId.isBlank()) {
                teamService.resolveTeamId(codeOrId).fold(
                    onSuccess = { realId -> resolvedTeamId = realId },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message ?: "Error al resolver equipo"
                        )
                        return@launch
                    }
                )
            }

            eventService.getEventsForTeam(resolvedTeamId).fold(
                onSuccess = { events ->
                    _uiState.value = _uiState.value.copy(events = events, isLoading = false)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar eventos"
                    )
                }
            )
        }
    }

    fun createEvent(codeOrId: String, name: String, type: EventType) {
        viewModelScope.launch {
            val userId = authService.getCurrentUserId()
            if (userId == null) {
                _uiState.value = _uiState.value.copy(error = "Usuario no autenticado")
                return@launch
            }
            _uiState.value = _uiState.value.copy(isCreating = true, error = null)

            // Resolver el ID real si aún no lo tenemos
            if (resolvedTeamId.isBlank()) {
                teamService.resolveTeamId(codeOrId).fold(
                    onSuccess = { realId -> resolvedTeamId = realId },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            error = e.message ?: "Error al resolver equipo"
                        )
                        return@launch
                    }
                )
            }

            eventService.addEvent(userId, resolvedTeamId, name, type).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        successMessage = "¡Evento creado!"
                    )
                    loadEvents(resolvedTeamId)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        error = e.message ?: "Error al crear evento"
                    )
                }
            )
        }
    }

    fun switchTeam(codeOrId: String) {
        resolvedTeamId = ""
        loadEvents(codeOrId)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
