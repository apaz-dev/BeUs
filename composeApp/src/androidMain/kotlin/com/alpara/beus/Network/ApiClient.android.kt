package com.alpara.beus.Network


import com.alpara.beus.Security.TokenManager
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.security.cert.X509Certificate
import javax.net.ssl.*

actual fun createHttpClient(tokenManager: TokenManager): HttpClient {
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, trustAllCerts, java.security.SecureRandom())
    }

    return HttpClient(OkHttp) {
        engine {
            config {
                sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                hostnameVerifier { _, _ -> true }
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
                    // Note: Using runBlocking here is acceptable for token loading
                    // as it's a quick local storage read operation
                    runBlocking {
                        val accessToken = tokenManager.getAccessToken()
                        accessToken?.let {
                            BearerTokens(accessToken = it, refreshToken = "")
                        }
                    }
                }
                
                sendWithoutRequest { request ->
                    // Only send token for API requests, not for login/register
                    !request.url.encodedPath.endsWith("/login/") && 
                    !request.url.encodedPath.endsWith("/register/")
                }
            }
        }
    }
}

//internal actual val BASE_URL: String = "https://10.0.2.2:8443"