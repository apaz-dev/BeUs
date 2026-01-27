package com.alpara.beus.Security

expect class TokenManager {
    suspend fun saveAccessToken(token: String)
    suspend fun getAccessToken(): String?
    suspend fun saveRefreshToken(token: String)
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
}

expect fun createTokenManager(): TokenManager
