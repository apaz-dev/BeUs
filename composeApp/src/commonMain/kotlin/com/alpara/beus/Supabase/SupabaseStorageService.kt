package com.alpara.beus.Supabase

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class SupabaseStorageService(httpClientEngine: HttpClientEngine) {

    private val bucket = "team-photos"
    private val url = SupabaseConfig.supabaseUrl
    private val anonKey = SupabaseConfig.supabaseAnonKey

    private val client = HttpClient(httpClientEngine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun uploadPhoto(
        imageBytes: ByteArray,
        teamId: String,
        eventId: String,
        photoId: String,
        authToken: String  // mantenido por compatibilidad, pero se usa anonKey para Supabase
    ): Result<String> {
        return try {
            val path = "$teamId/$eventId/$photoId.jpg"
            val uploadUrl = "$url/storage/v1/object/$bucket/$path"

            val response: HttpResponse = client.post(uploadUrl) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $anonKey")
                    append("apikey", anonKey)
                    append("x-upsert", "true")
                }
                contentType(ContentType.Image.JPEG)
                setBody(imageBytes)
            }

            if (response.status.isSuccess()) {
                val publicUrl = getPublicUrl(path)
                Result.success(publicUrl)
            } else {
                val body = response.bodyAsText()
                Result.failure(Exception("Error al subir imagen: ${response.status} - $body"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al subir imagen: ${e.message}"))
        }
    }

    suspend fun deletePhoto(
        teamId: String,
        eventId: String,
        photoId: String,
        authToken: String
    ): Result<Unit> {
        return try {
            val path = "$teamId/$eventId/$photoId.jpg"
            val deleteUrl = "$url/storage/v1/object/$bucket/$path"

            val response: HttpResponse = client.delete(deleteUrl) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $anonKey")
                    append("apikey", anonKey)
                }
            }

            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al borrar imagen: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al borrar imagen: ${e.message}"))
        }
    }

    fun getPublicUrl(storagePath: String): String {
        return "$url/storage/v1/object/public/$bucket/$storagePath"
    }
}
