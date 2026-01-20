package com.alpara.beus.network

import com.alpara.beus.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthService {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(
                    username = email,
                    password = password
                ))
            }
            Result.success(response.body<LoginResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
