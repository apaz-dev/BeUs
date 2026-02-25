package com.alpara.beus.Models

import com.alpara.beus.Utils.EventType
import kotlinx.serialization.Serializable

@Serializable
data class EventRCreate(
    val message: String
)

@Serializable
data class EventPCreate(
    val type: EventType
)

data class EventData(
    val id: String = "",
    val teamId: String = "",
    val name: String = "",
    val type: String = "",
    val createdAt: Long = 0L,
    val previewPhotos: List<String> = emptyList()  // URLs de las 3 últimas fotos
)

