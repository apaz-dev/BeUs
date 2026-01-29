package com.alpara.beus.Network

import com.alpara.beus.Models.*
import com.alpara.beus.Security.TokenManager
import io.ktor.client.call.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.text.get

class AuthService(private val tokenManager: TokenManager) {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    /*
        Login function: Mediante el email (tmb username) y una password realiza una peticion
        POST al endpoint /login/ del backend para autenticar al usuario. Si la autenticacion es
        exitosa, guarda los tokens de acceso y refresco utilizando el TokenManager.

        Se usa una clase serializada LoginRequest para validad el formato
         en el que los datos del cuerpo se envian
    */
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
            //tokenManager.saveAccessToken(loginResponse.accessToken)
            //tokenManager.saveRefreshToken(loginResponse.refreshToken)

            Result.success(loginResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /*
        Register function: Mediante el email, username y una password realiza una peticion
        POST al endpoint /register/ del backend para registrar al usuario.

        Se usa una clase serializada RegisterRequest para validad el formato
         en el que los datos del cuerpo se envian
    */
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

    suspend fun checkAuthStatus(): Boolean {
        return try {
            val response = client.get("$baseUrl/token/check"){
                contentType(ContentType.Application.Json)
            }
            if (response.status == HttpStatusCode.OK) {
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            return false
        }
    }
}