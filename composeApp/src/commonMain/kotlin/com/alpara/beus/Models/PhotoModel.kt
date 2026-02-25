package com.alpara.beus.Models

import kotlinx.serialization.Serializable

@Serializable
data class PhotoModel(
    val id: String = "",
    val teamId: String = "",
    val eventId: String = "",
    val uploadedBy: String = "",       // UID Firebase del autor
    val uploaderName: String = "",     // Nombre para mostrar
    val storagePath: String = "",      // path completo en el bucket
    val publicUrl: String = "",        // URL pública/firmada para mostrar la imagen
    val caption: String = "",          // Descripción opcional
    val createdAt: Long = 0L           // epoch millis
)
