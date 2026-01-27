package com.alpara.beus.Network

import com.alpara.beus.Security.TokenManager
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun createHttpClient(tokenManager: TokenManager): HttpClient

//internal expect val BASE_URL: String

val BASE_URL = "https://ripe-seals-lead-beus.loca.lt"

object ApiClient {
    private lateinit var tokenManager: TokenManager
    private var _httpClient: HttpClient? = null
    
    fun initialize(tokenManager: TokenManager) {
        this.tokenManager = tokenManager
        _httpClient = createHttpClient(tokenManager)
    }
    
    val httpClient: HttpClient
        get() = _httpClient ?: throw IllegalStateException("ApiClient not initialized. Call initialize() first.")

    fun getBaseUrl() = BASE_URL
}

/**
 * Determines if a request should exclude the Bearer token.
 * This prevents authentication loops and unnecessary token sending.
 */
internal fun shouldExcludeAuth(url: Url): Boolean {
    val path = url.encodedPath
    return path.endsWith("/login/") || path.endsWith("/register/")
}
