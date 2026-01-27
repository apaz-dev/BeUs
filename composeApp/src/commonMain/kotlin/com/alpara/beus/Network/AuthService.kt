package com.alpara.beus.Network

import com.alpara.beus.Models.*
import com.alpara.beus.Security.TokenManager
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthService(private val tokenManager: TokenManager) {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/login/") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(
                    username = email,
                    password = password
                ))
            }
            val loginResponse = response.body<LoginResponse>()

            // Save tokens
            tokenManager.saveAccessToken(loginResponse.accessToken)
            tokenManager.saveRefreshToken(loginResponse.refreshToken)

            Result.success(loginResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = client.post("$baseUrl/register/"){
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(
                    username = username,
                    email = email,
                    password = password
                ))
            }
            Result.success(response.body<RegisterResponse>())
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}