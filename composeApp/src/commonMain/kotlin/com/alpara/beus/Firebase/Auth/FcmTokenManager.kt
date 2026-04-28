package com.alpara.beus.Firebase.Auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

/**
 * Obtiene el token FCM del dispositivo. Implementación por plataforma.
 */
expect suspend fun getPlatformFcmToken(): String?

/**
 * Servicio común para registrar el FCM token en Firestore.
 */
class FcmTokenRegistrar {
    private val firestore = Firebase.firestore

    /**
     * Obtiene el token FCM y lo guarda en el perfil del usuario en Firestore.
     */
    suspend fun registerToken(userId: String): Result<Unit> {
        return try {
            val token = getPlatformFcmToken()
            if (token.isNullOrBlank()) {
                return Result.success(Unit) // No error, simplemente no hay token aún
            }

            firestore.collection("profiles").document(userId).update(
                mapOf("fcmToken" to token)
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al registrar token FCM: ${e.message}"))
        }
    }
}

