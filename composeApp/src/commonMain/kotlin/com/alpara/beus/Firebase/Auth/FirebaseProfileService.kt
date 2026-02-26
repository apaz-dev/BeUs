package com.alpara.beus.Firebase.Auth

import com.alpara.beus.Models.ProfilePrivate
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.Timestamp

class FirebaseProfileService {
    private val firestore = Firebase.firestore

    suspend fun getProfile(userId: String): Result<ProfilePrivate> {
        return try {
            val docSnapshot = firestore.collection("profiles").document(userId).get()
            
            if (docSnapshot.exists) {
                // Deserializar directamente al modelo ProfilePrivate
                val profile = docSnapshot.data(ProfilePrivate.serializer())
                Result.success(profile)
            } else {
                Result.failure(Exception("Perfil no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener perfil: ${e.message}"))
        }
    }
    
    suspend fun createProfile(userId: String, username: String, email: String): Result<Unit> {
        return try {
            val profileData = mapOf(
                "username" to username,
                "email" to email,
                "avatar_url" to "",
                "teams" to emptyList<Map<String, String>>(),
                "created_at" to Timestamp.now()
            )
            
            firestore.collection("profiles").document(userId).set(profileData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al crear perfil: ${e.message}"))
        }
    }
    
    suspend fun updateProfile(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("profiles").document(userId).update(updates)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar perfil: ${e.message}"))
        }
    }

    suspend fun deleteProfile(userId: String): Result<Unit> {
        return try {
            firestore.collection("profiles").document(userId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al eliminar perfil: ${e.message}"))
        }
    }


}
