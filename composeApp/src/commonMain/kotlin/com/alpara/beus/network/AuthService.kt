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
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("username=$email&password=$password")
            }
            Result.success(response.body<LoginResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<UserResponse> {
        return try {
            val response = client.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            Result.success(response.body<UserResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
