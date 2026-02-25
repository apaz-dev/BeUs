package com.alpara.beus.Backend

import com.alpara.beus.Security.TokenManager
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
actual fun createHttpClient(tokenManager: TokenManager): HttpClient {
    return HttpClient(Darwin) {
        engine {
            configureRequest {
                setAllowsExpensiveNetworkAccess(true) // Permitir acceso usando datos
                setAllowsConstrainedNetworkAccess(true) // Permitir acceso en redes con restricciones
            }

            handleChallenge { _, _, challenge, completionHandler ->
                val protectionSpace = challenge.protectionSpace // Obtener info del certificado
                // Es un reto de confianza del servidor?? (SPOILER: SI)
                if (protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust) {
                    val trust = protectionSpace.serverTrust // Obtener el objeto de confianza
                    if (trust != null) {
                        val credential = NSURLCredential.credentialForTrust(trust) // Dice: "si confiamos"
                        // No usar .toLong(), pasar directamente el valor enum
                        completionHandler(NSURLSessionAuthChallengeUseCredential, credential)
                    } else {
                        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null) // No confiar
                    }
                } else {
                    completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null) // No confiar
                }
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }.apply {
        // Hook para añadir el token actualizado antes de cada petición
        requestPipeline.intercept(HttpRequestPipeline.State) {
            val url = context.url
            if (!shouldExcludeAuth(url)) {
                val token = tokenManager.getAccessToken()
                if (!token.isNullOrBlank()) {
                    context.headers.append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }
}

//internal actual val BASE_URL: String = "https://localhost:8443"