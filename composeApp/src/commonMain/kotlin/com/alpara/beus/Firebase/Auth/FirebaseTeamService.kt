package com.alpara.beus.Firebase.Auth

import com.alpara.beus.Models.TeamCreateResponse
import com.alpara.beus.Models.TeamJoinResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import kotlin.random.Random

class FirebaseTeamService {

    private val firestore = Firebase.firestore

    private fun generateTeamCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return buildString(6) { repeat(6) { append(chars[Random.nextInt(chars.length)]) } }
    }

    suspend fun resolveTeamId(codeOrId: String): Result<String> {
        if (codeOrId.isBlank()) return Result.failure(Exception("ID de equipo vacío"))
        // Si parece un ID de Firestore (>8 chars), asumir que ya es el ID real
        if (codeOrId.length > 8) return Result.success(codeOrId)
        return try {
            val snap = firestore.collection("teamCodes").document(codeOrId.uppercase()).get()
            if (!snap.exists) return Result.failure(Exception("Código de equipo no encontrado: $codeOrId"))
            val teamId = snap.get<String>("team_id")
            Result.success(teamId)
        } catch (e: Exception) {
            Result.failure(Exception("Error al resolver teamId: ${e.message}"))
        }
    }

    suspend fun createTeam(currentUserId: String, teamName: String): Result<TeamCreateResponse> {
        return try {
            if (teamName.isBlank()) return Result.failure(Exception("El nombre no puede estar vacío"))
            if (teamName.length < 3) return Result.failure(Exception("El nombre debe tener al menos 3 caracteres"))

            val teamsCol = firestore.collection("teams")
            val codesCol = firestore.collection("teamCodes")

            val teamRef = teamsCol.document
            val teamId = teamRef.id

            var tries = 0

            while (tries < 10) {
                tries++
                val code = generateTeamCode()
                val codeRef = codesCol.document(code)

                // Leer ANTES de la transacción para evitar NullPointerException en iOS
                // (el bloque de runTransaction es síncrono en iOS y no puede suspenderse)
                val codeSnap: DocumentSnapshot = codeRef.get()
                if (codeSnap.exists) continue

                try {
                    firestore.runTransaction<Unit> {
                        // 1) Reservar código único (solo escrituras dentro de la transacción)
                        set(codeRef, mapOf("team_id" to teamId))

                        // 2) Crear team
                        set(
                            teamRef,
                            mapOf(
                                "name" to teamName,
                                "code" to code,
                                "owner_user_id" to currentUserId,
                                "created_at" to Timestamp.now()
                            )
                        )

                        // 3) Añadir creador como miembro OWNER
                        val memberRef = teamRef.collection("members").document(currentUserId)
                        set(
                            memberRef,
                            mapOf(
                                "role" to "OWNER",
                                "joined_at" to Timestamp.now()
                            )
                        )

                        // 4) Guardar el equipo en el perfil del usuario (como Map, Firestore no acepta objetos Kotlin)
                        val profileRef = firestore.collection("profiles").document(currentUserId)
                        val profileTeamMap = mapOf("name" to teamName, "join_code" to code, "team_id" to teamId)

                        update(profileRef, mapOf("teams" to FieldValue.arrayUnion(profileTeamMap)))
                    }

                    return Result.success(TeamCreateResponse("Equipo creado correctamente", code))

                } catch (e: Throwable) {
                    // Si hay colisión de código por condición de carrera, reintentar
                    if (e.message?.contains("ALREADY_EXISTS") == true ||
                        e.message?.contains("already-exists") == true) continue
                    throw e
                }
            }

            Result.failure(Exception("No se pudo generar código único, prueba de nuevo"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al crear equipo: ${e.message}"))
        }
    }

    suspend fun joinTeamByCode(currentUserId: String, joinCode: String): Result<TeamJoinResponse> {
        return try {
            val code = joinCode.trim().uppercase()
            if (code.isBlank()) return Result.failure(Exception("El código no puede estar vacío"))
            if (code.length < 6) return Result.failure(Exception("El código debe tener al menos 6 caracteres"))

            val codeRef = firestore.collection("teamCodes").document(code)

            // Leer ANTES de la transacción para evitar NullPointerException en iOS
            // (el bloque de runTransaction es síncrono en iOS y no puede suspenderse)
            val codeSnap: DocumentSnapshot = codeRef.get()
            if (!codeSnap.exists) return Result.failure(Exception("No existe un equipo con ese código"))

            val teamId = codeSnap.get<String>("team_id")

            val teamRef = firestore.collection("teams").document(teamId)
            val teamSnap: DocumentSnapshot = teamRef.get()
            if (!teamSnap.exists) return Result.failure(Exception("No existe un equipo con ese código"))

            val teamName = teamSnap.get<String>("name")

            firestore.runTransaction<Unit> {
                val memberRef = teamRef.collection("members").document(currentUserId)
                set(
                    memberRef,
                    mapOf(
                        "role" to "MEMBER",
                        "joined_at" to Timestamp.now()
                    )
                )

                val profileRef = firestore.collection("profiles").document(currentUserId)
                val profileTeamMap = mapOf("name" to teamName, "join_code" to code, "team_id" to teamId)

                update(profileRef, mapOf("teams" to FieldValue.arrayUnion(profileTeamMap)))
            }

            Result.success(TeamJoinResponse("Te has unido correctamente"))
        } catch (e: Throwable) {
            val msg = "Error al unirse al equipo: ${e.message}"
            Result.failure(Exception(msg))
        }
    }
}