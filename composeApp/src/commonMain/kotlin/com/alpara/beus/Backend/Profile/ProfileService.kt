package com.alpara.beus.Backend.Profile

import com.alpara.beus.Models.ProfilePrivate
import com.alpara.beus.Backend.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

class ProfileService {
    private val client = ApiClient.httpClient
    private val baseUrl = ApiClient.getBaseUrl()

    suspend fun getPrivate(): Result<ProfilePrivate>{

        return try {
            val response = client.get("$baseUrl/profile/me")


            if (response.status == HttpStatusCode.Unauthorized) {
                return Result.failure(Exception("Usuario o Contraseña Incorrectos"))
            }

            if (response.status == HttpStatusCode.ServiceUnavailable) {
                return Result.failure(Exception("Servidor no disponible, intentalo mas tarde"))
            }

            val profileResponse = response.body<ProfilePrivate>()

            Result.success(profileResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}