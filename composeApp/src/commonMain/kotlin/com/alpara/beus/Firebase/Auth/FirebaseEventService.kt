package com.alpara.beus.Firebase.Auth

import com.alpara.beus.Models.EventData
import com.alpara.beus.Models.EventRCreate
import com.alpara.beus.Models.RoleAssignment
import com.alpara.beus.Utils.EventRole
import com.alpara.beus.Utils.EventType
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class FirebaseEventService {
    private val firestore = Firebase.firestore

    // Crea un evento dentro de un equipo con nombre descriptivo y asigna roles.
    suspend fun addEvent(
        userId: String,
        teamId: String,
        name: String,
        type: String
    ): Result<EventRCreate> {
        return try {
            if (teamId.isBlank()) return Result.failure(Exception("No hay equipo seleccionado"))
            if (name.isBlank()) return Result.failure(Exception("El nombre del evento no puede estar vacío"))
            if (type.isBlank()) return Result.failure(Exception("El tipo de evento no puede estar vacío"))

            val eventsCol = firestore
                .collection("teams")
                .document(teamId)
                .collection("events")

            val docRef = eventsCol.document
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
            val calendarDate = "${today.year}-${today.monthNumber.toString().padStart(2, '0')}-${today.dayOfMonth.toString().padStart(2, '0')}"
            docRef.set(
                mapOf(
                    "id" to docRef.id,
                    "teamId" to teamId,
                    "name" to name,
                    "type" to type,
                    "user_id" to userId,
                    "created_at" to Timestamp.now(),
                    "calendarDate" to calendarDate
                )
            )

            // ── Asignar roles a todos los miembros del equipo ──
            assignRolesToMembers(teamId, docRef.id)

            Result.success(EventRCreate(message = "Evento creado correctamente"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al crear evento: ${e.message}"))
        }
    }

    suspend fun deleteEvent(teamId: String, eventId: String): Result<Unit> {
        return try {
            if (teamId.isBlank()) return Result.failure(Exception("No hay equipo seleccionado"))
            if (eventId.isBlank()) return Result.failure(Exception("Evento no valido"))

            firestore
    /**
     * Asigna roles (POLICIA, VLOGGER, CONTADOR) aleatoriamente a cada miembro del equipo
     * y guarda cada asignación en teams/{teamId}/events/{eventId}/roles/{userId}.
     * También guarda los tokens FCM disponibles para enviar notificaciones.
     */
    private suspend fun assignRolesToMembers(teamId: String, eventId: String) {
        val membersSnap = firestore
            .collection("teams")
            .document(teamId)
            .collection("members")
            .get()

        val memberIds = membersSnap.documents.map { it.id }
        if (memberIds.isEmpty()) return

        val roles = EventRole.entries
        val shuffledRoles = memberIds.indices.map { roles[it % roles.size] }.shuffled()

        val rolesCol = firestore
            .collection("teams")
            .document(teamId)
            .collection("events")
            .document(eventId)
            .collection("roles")

        memberIds.forEachIndexed { index, memberId ->
            val assignedRole = shuffledRoles[index]

            // Obtener username del perfil
            val username = try {
                val profileSnap = firestore.collection("profiles").document(memberId).get()
                if (profileSnap.exists) profileSnap.get<String>("username") else memberId
            } catch (_: Exception) { memberId }

            rolesCol.document(memberId).set(
                mapOf(
                    "userId" to memberId,
                    "username" to username,
                    "role" to assignedRole.name,
                    "assigned_at" to Timestamp.now()
                )
            )

            // Guardar datos de notificación pendiente para cada miembro
            try {
                val profileSnap = firestore.collection("profiles").document(memberId).get()
                if (profileSnap.exists) {
                    val fcmToken = try { profileSnap.get<String>("fcmToken") } catch (_: Exception) { null }
                    if (!fcmToken.isNullOrBlank()) {
                        val notifDocRef = firestore.collection("notificationQueue").document
                        notifDocRef.set(
                            mapOf(
                                "token" to fcmToken,
                                "title" to "🎲 ¡Nuevo rol asignado!",
                                "body" to "${assignedRole.emoji} TE HA TOCADO SER ${assignedRole.displayName.uppercase()}",
                                "teamId" to teamId,
                                "eventId" to eventId,
                                "userId" to memberId,
                                "role" to assignedRole.name,
                                "sent" to false,
                                "created_at" to Timestamp.now()
                            )
                        )
                    }
                }
            } catch (_: Exception) {
                // No bloquear la creación del evento por errores de notificación
            }
        }
    }

    /**
     * Obtiene todas las asignaciones de rol para un evento.
     */
    suspend fun getRolesForEvent(teamId: String, eventId: String): Result<List<RoleAssignment>> {
        return try {
            val rolesSnap = firestore
                .collection("teams")
                .document(teamId)
                .collection("events")
                .document(eventId)
                .delete()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al borrar evento: ${e.message}"))
        }
    }

    // Devuelve los eventos de un equipo (más recientes primero) con preview de fotos.
    suspend fun getEventsForTeam(teamId: String): Result<List<EventData>> {
                .collection("roles")
                .get()

            val roles = rolesSnap.documents.mapNotNull { doc ->
                try {
                    RoleAssignment(
                        userId = doc.get<String>("userId"),
                        username = doc.get<String>("username"),
                        role = doc.get<String>("role")
                    )
                } catch (_: Exception) { null }
            }

            Result.success(roles)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener roles: ${e.message}"))
        }
    }

    // Devuelve los eventos de un equipo (más recientes primero) con preview de fotos y rol del usuario.
    suspend fun getEventsForTeam(teamId: String, currentUserId: String? = null): Result<List<EventData>> {
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
                        createdAt = createdAt,
                        calendarDate = try { doc.get<String>("calendarDate") } catch (_: Exception) { null }
                    )
                } catch (_: Exception) { null }
            }.sortedByDescending { it.createdAt }

            // Para cada evento, cargar las 3 últimas fotos y el rol del usuario
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

                // Obtener rol del usuario actual para este evento
                val userRole = if (!currentUserId.isNullOrBlank()) {
                    try {
                        val roleDoc = firestore
                            .collection("teams")
                            .document(teamId)
                            .collection("events")
                            .document(event.id)
                            .collection("roles")
                            .document(currentUserId)
                            .get()
                        if (roleDoc.exists) roleDoc.get<String>("role") else null
                    } catch (_: Exception) { null }
                } else null

                event.copy(previewPhotos = previews, currentUserRole = userRole)
            }

            Result.success(eventsWithPreviews)
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener eventos: ${e.message}"))
        }
    }
}

    /**
     * Envía una notificación de prueba al usuario actual escribiendo en notificationQueue.
     */
    suspend fun sendTestNotification(userId: String): Result<Unit> {
        return try {
            val profileSnap = firestore.collection("profiles").document(userId).get()
            if (!profileSnap.exists) return Result.failure(Exception("Perfil no encontrado"))

            val fcmToken = try { profileSnap.get<String>("fcmToken") } catch (_: Exception) { null }
            if (fcmToken.isNullOrBlank()) {
                return Result.failure(Exception("No hay token de notificaciones registrado"))
            }

            val testDocRef = firestore.collection("notificationQueue").document
            testDocRef.set(
                mapOf(
                    "token" to fcmToken,
                    "title" to "🔔 Notificación de prueba",
                    "body" to "¡Las notificaciones push funcionan correctamente!",
                    "userId" to userId,
                    "sent" to false,
                    "test" to true,
                    "created_at" to Timestamp.now()
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al enviar notificación de prueba: ${e.message}"))
        }
    }
}
