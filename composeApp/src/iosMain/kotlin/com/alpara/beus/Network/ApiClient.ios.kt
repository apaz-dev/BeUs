package com.alpara.beus.Network

import com.alpara.beus.Security.TokenManager
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import platform.Foundation.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import kotlinx.coroutines.runBlocking

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
        install(Auth) {
            bearer {
                loadTokens {
                    runBlocking {
                        val accessToken = tokenManager.getAccessToken()
                        accessToken?.let {
                            BearerTokens(accessToken = it, refreshToken = "")
                        }
                    }
                }

                sendWithoutRequest { request ->
                    !shouldExcludeAuth(request.url)
                }
            }
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
}

//internal actual val BASE_URL: String = "https://localhost:8443"