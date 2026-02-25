package com.alpara.beus.Backend

import com.alpara.beus.Security.TokenManager
import io.ktor.client.*
import io.ktor.http.*

/*
    Aqui se cra un cliente HTTP utilizando `expect`/`actual` mediante ktor para manejar
    las solicitudes de red en diferentes plataformas (Android/iOS).

 */
expect fun createHttpClient(tokenManager: TokenManager): HttpClient

//internal expect val BASE_URL: String

val BASE_URL = "https://ripe-seals-lead-beus.loca.lt"

object ApiClient {


    private lateinit var tokenManager: TokenManager // lateinit declara una variables (MUTABLES) que sera inicializada despues
    private var _httpClient: HttpClient? = null

    fun initialize(tokenManager: TokenManager) {
        this.tokenManager = tokenManager
        _httpClient = createHttpClient(tokenManager)
    }

    val httpClient: HttpClient
        // Getter personalizado para el httClient
        get() = _httpClient ?: throw IllegalStateException("ApiClient not initialized. Call initialize() first.")

    fun getBaseUrl() = BASE_URL
}

// Para que /login/ y /register/ no necesiten token de autenticacion
internal fun shouldExcludeAuth(url: URLBuilder): Boolean {
    val path = url.encodedPath
    return path.endsWith("/login/") || path.endsWith("/register/")
}