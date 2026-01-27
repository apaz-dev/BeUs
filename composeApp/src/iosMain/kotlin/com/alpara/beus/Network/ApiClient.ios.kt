package com.alpara.beus.Network

import com.alpara.beus.Security.TokenManager
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
actual fun createHttpClient(tokenManager: TokenManager): HttpClient {
    return HttpClient(Darwin) {
        engine {
            configureRequest {
                setAllowsExpensiveNetworkAccess(true)
                setAllowsConstrainedNetworkAccess(true)
            }

            handleChallenge { _, _, challenge, completionHandler ->
                val protectionSpace = challenge.protectionSpace
                if (protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust) {
                    val trust = protectionSpace.serverTrust
                    if (trust != null) {
                        val credential = NSURLCredential.credentialForTrust(trust)
                        completionHandler(NSURLSessionAuthChallengeUseCredential.toLong(), credential)
                    } else {
                        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling.toLong(), null)
                    }
                } else {
                    completionHandler(NSURLSessionAuthChallengePerformDefaultHandling.toLong(), null)
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

        install(Auth) {
            bearer {
                loadTokens {
                    /* Using runBlocking here is intentional and safe because:
                     * 1. This is called on Ktor's IO dispatcher, not the main thread
                     * 2. Token retrieval is a fast local Keychain read (< 10ms)
                     * 3. iOS Keychain operations are synchronous by nature
                     * 4. The Auth plugin's loadTokens callback is not a suspend function
                     * 5. This runs once per HTTP client initialization, not per request
                     */
                    runBlocking {
                        val accessToken = tokenManager.getAccessToken()
                        accessToken?.let {
                            BearerTokens(accessToken = it, refreshToken = "")
                        }
                    }
                }
                
                sendWithoutRequest { request ->
                    // Use common function to determine if auth should be excluded
                    !shouldExcludeAuth(request.url)
                }
            }
        }
    }
}

//internal actual val BASE_URL: String = "https://localhost:8443"