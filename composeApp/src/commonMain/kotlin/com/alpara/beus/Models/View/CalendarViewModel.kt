package com.alpara.beus.Models.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Firebase.Auth.FirebaseAuthService
import com.alpara.beus.Firebase.Auth.FirebaseEventService
import com.alpara.beus.Firebase.Auth.FirebasePhotoService
import com.alpara.beus.Firebase.Auth.FirebaseTeamService
import com.alpara.beus.Models.EventData
import com.alpara.beus.Models.PhotoModel
import com.alpara.beus.Supabase.SupabaseStorageService
import com.alpara.beus.Supabase.createSupabaseHttpEngine
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

data class DayEvents(
    val date: LocalDate,
    val events: List<EventData> = emptyList(),
    val photos: Map<String, List<PhotoModel>> = emptyMap()
)

data class CalendarUiState(
    val currentMonth: Int = 1,
    val currentYear: Int = 2024,
    val today: LocalDate = LocalDate(2024, 1, 1),
    val selectedDate: LocalDate? = null,
    val dayEvents: Map<LocalDate, DayEvents> = emptyMap(),
    val calendarDayPhotos: Map<LocalDate, List<PhotoModel>> = emptyMap(),
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val activeTeamId: String = ""
)

class CalendarViewModel : ViewModel() {

    private val authService = FirebaseAuthService()
    private val eventService = FirebaseEventService()
    private val photoService = FirebasePhotoService()
    private val teamService = FirebaseTeamService()
    private val storageService = SupabaseStorageService(createSupabaseHttpEngine())

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        initializeCurrentMonth()
        loadTeamAndEvents()
    }

    fun initializeCurrentMonth() {
        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        _uiState.value = _uiState.value.copy(
            currentMonth = now.monthNumber,
            currentYear = now.year,
            today = now
        )
    }

    private fun loadTeamAndEvents() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val userId = authService.getCurrentUserId()
                if (userId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        dayEvents = emptyMap(),
                        calendarDayPhotos = emptyMap(),
                        error = null
                    )
                    return@launch
                }

                val codeOrId = try {
                    com.alpara.beus.BarNav.ActiveTeamArgs.teamId.takeIf { it.isNotEmpty() }
                } catch (_: Exception) {
                    null
                }

                if (codeOrId.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        dayEvents = emptyMap(),
                        calendarDayPhotos = emptyMap(),
                        error = null
                    )
                    return@launch
                }

                val teamId = teamService.resolveTeamId(codeOrId).getOrElse {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        dayEvents = emptyMap(),
                        calendarDayPhotos = emptyMap(),
                        error = "No se pudo resolver el equipo activo"
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(activeTeamId = teamId)

                loadEventsForTeam(teamId)
                loadCalendarPhotosForCurrentMonth(teamId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error cargando calendario"
                )
            }
        }
    }

    private fun loadEventsForTeam(teamId: String) {
        viewModelScope.launch {
            try {
                val eventsResult = eventService.getEventsForTeam(teamId)
                val events = eventsResult.getOrNull() ?: emptyList()

                val dayEventsMap = mutableMapOf<LocalDate, DayEvents>()

                for (event in events) {
                    try {
                        val eventDate = try {
                            // Prioriza la fecha explícita guardada con el evento.
                            parseEventCalendarDate(event.calendarDate)
                        } catch (_: Exception) {
                            // Eventos antiguos no siempre tienen calendarDate.
                            kotlinx.datetime.Instant
                                .fromEpochMilliseconds(event.createdAt)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date
                        }

                        val photosResult = photoService.getPhotos(teamId, event.id)
                        val photos = photosResult.getOrNull() ?: emptyList()

                        val currentDayEvent = dayEventsMap[eventDate] ?: DayEvents(date = eventDate)

                        dayEventsMap[eventDate] = currentDayEvent.copy(
                            events = currentDayEvent.events + event,
                            photos = currentDayEvent.photos + (event.id to photos)
                        )
                    } catch (_: Exception) {
                    }
                }

                _uiState.value = _uiState.value.copy(
                    dayEvents = dayEventsMap,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar eventos: ${e.message}"
                )
            }
        }
    }

    private fun loadCalendarPhotosForCurrentMonth(teamId: String) {
        viewModelScope.launch {
            try {
                val year = _uiState.value.currentYear
                val month = _uiState.value.currentMonth
                val daysInMonth = getDaysInMonthInternal(year, month)

                val results = (1..daysInMonth).map { day ->
                    async {
                        val date = LocalDate(year, month, day)
                        val eventId = buildCalendarEventId(date)
                        val photos = photoService.getPhotos(teamId, eventId).getOrNull() ?: emptyList()
                        date to photos
                    }
                }.awaitAll()

                val map = results
                    .filter { it.second.isNotEmpty() }
                    .associate { it.first to it.second }

                _uiState.value = _uiState.value.copy(
                    calendarDayPhotos = map,
                    isLoading = false
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun loadPhotosForDate(date: LocalDate) {
        val teamId = _uiState.value.activeTeamId
        if (teamId.isBlank()) return

        viewModelScope.launch {
            try {
                val eventId = buildCalendarEventId(date)
                val photos = photoService.getPhotos(teamId, eventId).getOrNull() ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    calendarDayPhotos = _uiState.value.calendarDayPhotos + (date to photos)
                )
            } catch (_: Exception) {
            }
        }
    }

    fun uploadPhotoToDate(
        date: LocalDate,
        imageBytes: ByteArray,
        caption: String = ""
    ) {
        val teamId = _uiState.value.activeTeamId
        if (teamId.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "No hay equipo activo")
            return
        }

        viewModelScope.launch {
            try {
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

                _uiState.value = _uiState.value.copy(
                    isUploadingPhoto = true,
                    error = null,
                    successMessage = null
                )

                val eventId = buildCalendarEventId(date)
                val photoId = "${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(10000, 99999)}"

                storageService.uploadPhoto(
                    imageBytes = imageBytes,
                    teamId = teamId,
                    eventId = eventId,
                    photoId = photoId,
                    authToken = token
                ).fold(
                    onSuccess = { publicUrl ->
                        val photo = PhotoModel(
                            id = photoId,
                            teamId = teamId,
                            eventId = eventId,
                            uploadedBy = userId,
                            uploaderName = "",
                            storagePath = "$teamId/$eventId/$photoId.jpg",
                            publicUrl = publicUrl,
                            caption = caption,
                            createdAt = Clock.System.now().toEpochMilliseconds()
                        )

                        photoService.addPhoto(photo).fold(
                            onSuccess = {
                                loadPhotosForDate(date)
                                _uiState.value = _uiState.value.copy(
                                    isUploadingPhoto = false,
                                    successMessage = "Foto añadida al día ${date.dayOfMonth}/${date.monthNumber}/${date.year}"
                                )
                            },
                            onFailure = { e ->
                                _uiState.value = _uiState.value.copy(
                                    isUploadingPhoto = false,
                                    error = "La imagen se subió, pero falló la metadata: ${e.message}"
                                )
                            }
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isUploadingPhoto = false,
                            error = e.message ?: "Error subiendo foto"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploadingPhoto = false,
                    error = e.message ?: "Error subiendo la foto"
                )
            }
        }
    }

    fun deletePhotoFromDate(date: LocalDate, photo: PhotoModel) {
        val teamId = _uiState.value.activeTeamId
        if (teamId.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "No hay equipo activo")
            return
        }

        viewModelScope.launch {
            try {
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

                _uiState.value = _uiState.value.copy(error = null, successMessage = null)

                val eventId = buildCalendarEventId(date)
                storageService.deletePhoto(teamId, eventId, photo.id, token).fold(
                    onSuccess = {
                        photoService.deletePhoto(teamId, eventId, photo.id).fold(
                            onSuccess = {
                                loadPhotosForDate(date)
                                _uiState.value = _uiState.value.copy(
                                    successMessage = "Foto eliminada"
                                )
                            },
                            onFailure = { e ->
                                _uiState.value = _uiState.value.copy(
                                    error = "Error al borrar metadata: ${e.message}"
                                )
                            }
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            error = e.message ?: "Error al borrar la foto"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error borrando la foto"
                )
            }
        }
    }

    fun goToPreviousMonth() {
        val state = _uiState.value
        val (newMonth, newYear) = if (state.currentMonth == 1) {
            12 to (state.currentYear - 1)
        } else {
            (state.currentMonth - 1) to state.currentYear
        }

        _uiState.value = _uiState.value.copy(
            currentMonth = newMonth,
            currentYear = newYear
        )

        reloadCurrentMonth()
    }

    fun goToNextMonth() {
        val state = _uiState.value
        val (newMonth, newYear) = if (state.currentMonth == 12) {
            1 to (state.currentYear + 1)
        } else {
            (state.currentMonth + 1) to state.currentYear
        }

        _uiState.value = _uiState.value.copy(
            currentMonth = newMonth,
            currentYear = newYear
        )

        reloadCurrentMonth()
    }

    private fun reloadCurrentMonth() {
        val teamId = _uiState.value.activeTeamId
        if (teamId.isBlank()) return

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = null,
            dayEvents = emptyMap(),
            calendarDayPhotos = emptyMap()
        )

        loadEventsForTeam(teamId)
        loadCalendarPhotosForCurrentMonth(teamId)
    }

    fun hasEventsOnDate(date: LocalDate): Boolean {
        return _uiState.value.dayEvents.containsKey(date)
    }

    fun hasPhotosOnDate(date: LocalDate): Boolean {
        return !_uiState.value.calendarDayPhotos[date].isNullOrEmpty()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            successMessage = null
        )
    }

    fun getCurrentUserId(): String? = authService.getCurrentUserId()

    private fun buildCalendarEventId(date: LocalDate): String {
        return "calendar_${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
    }

    private fun parseEventCalendarDate(calendarDate: String?): LocalDate {
        if (calendarDate.isNullOrBlank()) throw IllegalArgumentException("Sin fecha de calendario")
        return LocalDate.parse(calendarDate)
    }

    private fun getDaysInMonthInternal(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 31
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    fun refreshForActiveTeam(teamCodeOrId: String? = null) {
        val active = teamCodeOrId?.takeIf { it.isNotBlank() }
            ?: try {
                com.alpara.beus.BarNav.ActiveTeamArgs.teamId.takeIf { it.isNotBlank() }
            } catch (_: Exception) {
                null
            }

        if (active.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                dayEvents = emptyMap(),
                calendarDayPhotos = emptyMap(),
                activeTeamId = "",
                error = null
            )
            return
        }

        if (active == _uiState.value.activeTeamId &&
            (_uiState.value.dayEvents.isNotEmpty() || _uiState.value.calendarDayPhotos.isNotEmpty())
        ) {
            return
        }

        loadTeamAndEvents()
    }
}
