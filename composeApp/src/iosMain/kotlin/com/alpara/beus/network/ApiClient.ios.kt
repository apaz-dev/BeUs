package com.alpara.beus.network

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
actual fun createHttpClient(): HttpClient {
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
    }
}
