package com.alpara.beus.Firebase.Auth

import com.alpara.beus.Models.EventData
import com.alpara.beus.Models.EventRCreate
import com.alpara.beus.Utils.EventType
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore

class FirebaseEventService {
    private val firestore = Firebase.firestore

    // Crea un evento dentro de un equipo con nombre descriptivo.
    suspend fun addEvent(
        userId: String,
        teamId: String,
        name: String,
        type: EventType
    ): Result<EventRCreate> {
        return try {
            if (teamId.isBlank()) return Result.failure(Exception("No hay equipo seleccionado"))
            if (name.isBlank()) return Result.failure(Exception("El nombre del evento no puede estar vacío"))

            val eventsCol = firestore
                .collection("teams")
                .document(teamId)
                .collection("events")

            val docRef = eventsCol.document
            docRef.set(
                mapOf(
                    "id" to docRef.id,
                    "teamId" to teamId,
                    "name" to name,
                    "type" to type.toString(),
                    "user_id" to userId,
                    "created_at" to Timestamp.now()
                )
            )

            Result.success(EventRCreate(message = "Evento creado correctamente"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al crear evento: ${e.message}"))
        }
    }

    // Devuelve los eventos de un equipo (más recientes primero) con preview de fotos.
    suspend fun getEventsForTeam(teamId: String): Result<List<EventData>> {
        return try {
            if (teamId.isBlank()) return Result.success(emptyList())

            val eventsCol = firestore
                .collection("teams")
                .document(teamId)
                .collection("events")

            val snapshot = eventsCol.get()

            val events = snapshot.documents.mapNotNull { doc: DocumentSnapshot ->
                try {
                    val createdAt = try {
                        val ts = doc.get<Timestamp>("created_at")
                        ts.seconds * 1000L + ts.nanoseconds / 1_000_000L
                    } catch (_: Exception) { 0L }
                    EventData(
                        id = doc.get<String>("id"),
                        teamId = teamId,
                        name = doc.get<String>("name"),
                        type = doc.get<String>("type"),
                        createdAt = createdAt
                    )
                } catch (_: Exception) { null }
            }.sortedByDescending { it.createdAt }

            // Para cada evento, cargar las 3 últimas fotos
            val eventsWithPreviews = events.map { event ->
                val photosSnap = firestore
                    .collection("teams")
                    .document(teamId)
                    .collection("events")
                    .document(event.id)
                    .collection("photos")
                    .get()

                val previews = photosSnap.documents.mapNotNull { p: DocumentSnapshot ->
                    try { p.get<String>("publicUrl") } catch (_: Exception) { null }
                }.takeLast(3).reversed()

                event.copy(previewPhotos = previews)
            }

            Result.success(eventsWithPreviews)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener eventos: ${e.message}"))
        }
    }
}