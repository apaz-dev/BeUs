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
                    /* Using runBlocking here is intentional and safe because:
                     * 1. This is called on Ktor's IO dispatcher, not the main thread
                     * 2. Token retrieval is a fast local storage read (< 10ms)
                     * 3. EncryptedSharedPreferences operations are already synchronous
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

//internal actual val BASE_URL: String = "https://10.0.2.2:8443"