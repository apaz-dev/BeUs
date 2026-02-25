package com.alpara.beus.Security

/*
 Aqui se gestiona el almacenamiento de los tokens de autenticacion mediante `expect`/`actual`

 Con `expect` me quiero referir a q esa clase estara implementada en la plataforma en la que se
 este ejecutando (android/ios)

 Con `actual` es la implementacion concreta de esa clase en cada plataforma
 */

expect class TokenManager {
    suspend fun saveAccessToken(token: String)
    suspend fun getAccessToken(): String?
    suspend fun saveRefreshToken(token: String)
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
}

expect fun createTokenManager(): TokenManager