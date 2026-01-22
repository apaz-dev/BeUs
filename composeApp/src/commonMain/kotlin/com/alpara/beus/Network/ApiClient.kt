package com.alpara.beus.Network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun createHttpClient(): HttpClient

object ApiClient {
    private const val BASE_URL = "https://localhost:8443"

    val httpClient = createHttpClient()

    fun getBaseUrl() = BASE_URL
}