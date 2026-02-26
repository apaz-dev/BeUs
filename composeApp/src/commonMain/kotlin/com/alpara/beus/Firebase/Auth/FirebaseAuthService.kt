package com.alpara.beus.Firebase.Auth

import com.alpara.beus.Models.LoginResponse
import com.alpara.beus.Models.RegisterResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class FirebaseAuthService {
    private val auth = Firebase.auth

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password)
            val user = result.user

            if (user != null) {
                val token = user.getIdToken(false) ?: ""
                Result.success(LoginResponse(
                    accessToken = token,
                    refreshToken = user.uid
                ))
            } else {
                Result.failure(Exception("Usuario o Contraseña Incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Usuario o Contraseña Incorrectos: ${e.message}"))
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password)
            val user = result.user

            if (user != null) {
                user.updateProfile(displayName = username)

                Result.success(RegisterResponse(
                    username = username
                ))
            } else {
                Result.failure(Exception("Error al crear usuario"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al registrar: ${e.message}"))
        }
    }

    fun checkAuthStatus(): Boolean {
        return auth.currentUser != null
    }

    suspend fun logout() {
        auth.signOut()
    }

    suspend fun getCurrentUserToken(): String? {
        return try {
            auth.currentUser?.getIdToken(false)
        } catch (_: Exception) {
            null
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                user.updatePassword(newPassword)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Usuario no autenticado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar contraseña: ${e.message}"))
        }
    }

    suspend fun updateDisplayName(newDisplayName: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                user.updateProfile(displayName = newDisplayName)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Usuario no autenticado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar nombre: ${e.message}"))
        }
    }

    suspend fun reauthenticate(password: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("Usuario no autenticado"))
            }
            if (user.email == null) {
                return Result.failure(Exception("No se encontró el email del usuario"))
            }

            val credential = dev.gitlive.firebase.auth.EmailAuthProvider.credential(user.email!!, password)
            user.reauthenticate(credential)
            Result.success(Unit)
        } catch (e: Exception) {
            // Capturar mensaje específico de Firebase
            val errorMessage = when {
                e.message?.contains("wrong-password", ignoreCase = true) == true -> "Contraseña incorrecta"
                e.message?.contains("invalid-credential", ignoreCase = true) == true -> "Contraseña incorrecta"
                e.message?.contains("too-many-requests", ignoreCase = true) == true -> "Demasiados intentos. Intenta más tarde"
                else -> "Contraseña incorrecta: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun deleteAccount(password: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("Usuario no autenticado"))
            }

            // Re-autenticar antes de eliminar
            val reauthResult = reauthenticate(password)
            if (reauthResult.isFailure) {
                return Result.failure(reauthResult.exceptionOrNull() ?: Exception("Error en re-autenticación"))
            }

            // Si la re-autenticación es exitosa, eliminar la cuenta
            user.delete()
            Result.success(Unit)
        } catch (e: Exception) {
            // Capturar mensajes específicos de Firebase
            val errorMessage = when {
                e.message?.contains("requires-recent-login", ignoreCase = true) == true ->
                    "Necesitas volver a iniciar sesión antes de eliminar tu cuenta"
                e.message?.contains("permission", ignoreCase = true) == true ->
                    "Error de permisos: ${e.message}"
                else -> "Error al eliminar cuenta: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}
