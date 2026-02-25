package com.alpara.beus.Models.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Firebase.Auth.FirebaseAuthService
import com.alpara.beus.Firebase.Auth.FirebasePhotoService
import com.alpara.beus.Models.PhotoModel
import com.alpara.beus.Supabase.SupabaseStorageService
import com.alpara.beus.Supabase.createSupabaseHttpEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class PhotoGalleryUiState(
    val photos: List<PhotoModel> = emptyList(),
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@OptIn(ExperimentalTime::class)
class PhotoViewModel : ViewModel() {

    private val authService = FirebaseAuthService()
    private val photoService = FirebasePhotoService()
    private val storageService = SupabaseStorageService(createSupabaseHttpEngine())

    private val _uiState = MutableStateFlow(PhotoGalleryUiState())
    val uiState: StateFlow<PhotoGalleryUiState> = _uiState.asStateFlow()
    fun loadPhotos(teamId: String, eventId: String) {
        if (teamId.isBlank() || eventId.isBlank()) {
            _uiState.value = _uiState.value.copy(isLoading = false, photos = emptyList())
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            photoService.getPhotos(teamId, eventId).fold(
                onSuccess = { photos ->
                    _uiState.value = _uiState.value.copy(photos = photos, isLoading = false)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar fotos"
                    )
                }
            )
        }
    }

    fun uploadPhoto(
        imageBytes: ByteArray,
        teamId: String,
        eventId: String,
        caption: String = ""
    ) {
        if (teamId.isBlank() || eventId.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "No hay equipo o evento seleccionado")
            return
        }
        viewModelScope.launch {
            val userId = authService.getCurrentUserId()
            if (userId == null) {
                _uiState.value = _uiState.value.copy(error = "Usuario no autenticado")
                return@launch
            }

            val token = authService.getCurrentUserToken()
            if (token.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(error = "No se pudo obtener el token")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isUploading = true, error = null)

            // Generar ID único usando timestamp + random
            val photoId = "${Clock.System.now().toEpochMilliseconds()}_${(0..99999).random()}"

            storageService.uploadPhoto(imageBytes, teamId, eventId, photoId, token).fold(
                onSuccess = { publicUrl ->
                    val photo = PhotoModel(
                        id = photoId,
                        teamId = teamId,
                        eventId = eventId,
                        uploadedBy = userId,
                        uploaderName = "", // se puede rellenar con el display name si se quiere
                        storagePath = "$teamId/$eventId/$photoId.jpg",
                        publicUrl = publicUrl,
                        caption = caption,
                        createdAt = Clock.System.now().toEpochMilliseconds()
                    )
                    photoService.addPhoto(photo).fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isUploading = false,
                                successMessage = "Foto subida correctamente"
                            )
                            loadPhotos(teamId, eventId)
                        },
                        onFailure = { e ->
                            _uiState.value = _uiState.value.copy(
                                isUploading = false,
                                error = "Foto subida pero error guardando metadata: ${e.message}"
                            )
                        }
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        error = e.message ?: "Error al subir la foto"
                    )
                }
            )
        }
    }

    fun deletePhoto(photo: PhotoModel, teamId: String, eventId: String) {
        viewModelScope.launch {
            val userId = authService.getCurrentUserId()
            if (userId == null || userId != photo.uploadedBy) {
                _uiState.value = _uiState.value.copy(error = "No tienes permiso para borrar esta foto")
                return@launch
            }

            val token = authService.getCurrentUserToken()
            if (token.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(error = "No se pudo obtener el token")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            storageService.deletePhoto(teamId, eventId, photo.id, token).fold(
                onSuccess = {
                    photoService.deletePhoto(teamId, eventId, photo.id).fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                successMessage = "Foto eliminada"
                            )
                            loadPhotos(teamId, eventId)
                        },
                        onFailure = { e ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Error al borrar metadata: ${e.message}"
                            )
                        }
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al borrar la foto"
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }

    fun getCurrentUserId(): String? = authService.getCurrentUserId()
}
