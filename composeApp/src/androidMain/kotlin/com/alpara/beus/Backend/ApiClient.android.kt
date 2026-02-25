package com.alpara.beus.Backend


import com.alpara.beus.Security.TokenManager
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.security.cert.X509Certificate
import javax.net.ssl.*

actual fun createHttpClient(tokenManager: TokenManager): HttpClient {
    // Configurar un TrustManager que confíe en todos los certificados
    // (Al principio teniamos el backend con un certificado autofirmado)
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

//internal actual val BASE_URL: String = "https://10.0.2.2:8443"