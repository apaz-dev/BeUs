package com.alpara.beus.Models.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Firebase.Auth.FirebaseAuthService
import com.alpara.beus.Firebase.Auth.FirebaseEventService
import com.alpara.beus.Firebase.Auth.FirebasePhotoService
import com.alpara.beus.Firebase.Auth.FirebaseTeamService
import com.alpara.beus.Models.EventData
import com.alpara.beus.Supabase.SupabaseStorageService
import com.alpara.beus.Supabase.createSupabaseHttpEngine
import com.alpara.beus.Models.RoleAssignment
import com.alpara.beus.Utils.EventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventListUiState(
    val events: List<EventData> = emptyList(),
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val deletingEventId: String? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class EventListViewModel : ViewModel() {

    private val authService = FirebaseAuthService()
    private val eventService = FirebaseEventService()
    private val photoService = FirebasePhotoService()
    private val teamService = FirebaseTeamService()
    private val storageService = SupabaseStorageService(createSupabaseHttpEngine())

    private val _uiState = MutableStateFlow(EventListUiState())
    val uiState: StateFlow<EventListUiState> = _uiState.asStateFlow()

    // Roles del evento seleccionado
    private val _eventRoles = MutableStateFlow<List<RoleAssignment>>(emptyList())
    val eventRoles: StateFlow<List<RoleAssignment>> = _eventRoles.asStateFlow()

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

            val currentUserId = authService.getCurrentUserId()

            eventService.getEventsForTeam(resolvedTeamId, currentUserId).fold(
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

    fun loadRolesForEvent(teamId: String, eventId: String) {
        viewModelScope.launch {
            eventService.getRolesForEvent(teamId, eventId).fold(
                onSuccess = { roles -> _eventRoles.value = roles },
                onFailure = { _eventRoles.value = emptyList() }
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
                        successMessage = "¡Evento creado! Roles asignados 🎲"
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

    fun deleteEvent(codeOrId: String, event: EventData) {
        viewModelScope.launch {
            val userId = authService.getCurrentUserId()
            if (userId == null) {
                _uiState.value = _uiState.value.copy(error = "Usuario no autenticado")
                return@launch
            }
            if (event.id.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "Evento no valido")
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                deletingEventId = event.id,
                error = null,
                successMessage = null
            )

            if (resolvedTeamId.isBlank()) {
                teamService.resolveTeamId(codeOrId).fold(
                    onSuccess = { realId -> resolvedTeamId = realId },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            deletingEventId = null,
                            error = e.message ?: "Error al resolver equipo"
                        )
                        return@launch
                    }
                )
            }

            val token = authService.getCurrentUserToken()
            if (token.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    deletingEventId = null,
                    error = "No se pudo obtener el token"
                )
                return@launch
            }

            val photos = photoService.getPhotos(resolvedTeamId, event.id).getOrElse { e ->
                _uiState.value = _uiState.value.copy(
                    deletingEventId = null,
                    error = e.message ?: "Error al cargar fotos del evento"
                )
                return@launch
            }

            for (photo in photos) {
                storageService.deletePhoto(resolvedTeamId, event.id, photo.id, token).getOrElse { e ->
                    _uiState.value = _uiState.value.copy(
                        deletingEventId = null,
                        error = e.message ?: "Error al borrar una foto del evento"
                    )
                    return@launch
                }

                photoService.deletePhoto(resolvedTeamId, event.id, photo.id).getOrElse { e ->
                    _uiState.value = _uiState.value.copy(
                        deletingEventId = null,
                        error = e.message ?: "Error al borrar metadata de una foto"
                    )
                    return@launch
                }
            }

            eventService.deleteEvent(resolvedTeamId, event.id).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        events = _uiState.value.events.filterNot { it.id == event.id },
                        deletingEventId = null,
                        successMessage = "Evento eliminado"
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        deletingEventId = null,
                        error = e.message ?: "Error al borrar evento"
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
