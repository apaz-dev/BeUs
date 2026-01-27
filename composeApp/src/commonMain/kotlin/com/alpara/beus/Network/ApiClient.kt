package com.alpara.beus.Network

import com.alpara.beus.Security.TokenManager
import io.ktor.client.*
import io.ktor.http.*

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

internal fun shouldExcludeAuth(url: URLBuilder): Boolean {
    val path = url.encodedPath
    return path.endsWith("/login/") || path.endsWith("/register/")
}