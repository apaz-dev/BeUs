package com.alpara.beus.Models.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpara.beus.Backend.Event.EventService
import com.alpara.beus.Models.EventRCreate
import com.alpara.beus.Utils.EventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val eventService = EventService()

    private val _eventState = MutableStateFlow<EventState>(EventState.Loading)
    val eventState: StateFlow<EventState> = _eventState.asStateFlow()

    fun addEvent(type: EventType) {
        viewModelScope.launch {
            _eventState.value = EventState.Loading

            eventService.addEvent(type).fold(
                onSuccess = { eventResponse ->
                    _eventState.value = EventState.Success(eventResponse)
                },
                onFailure = { error ->
                    _eventState.value = EventState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }
}

sealed class EventState {
    data object Loading : EventState()
    data class Success(val profile: EventRCreate) : EventState()
    data class Error(val message: String) : EventState()
}
