package com.alpara.beus.Network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun createHttpClient(): HttpClient

//internal expect val BASE_URL: String

val BASE_URL = "https://ripe-seals-lead-beus.loca.lt"

object ApiClient {
    val httpClient = createHttpClient()

    fun getBaseUrl() = BASE_URL
}