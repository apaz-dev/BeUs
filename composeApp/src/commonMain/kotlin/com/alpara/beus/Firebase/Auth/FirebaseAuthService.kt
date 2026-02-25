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
}
