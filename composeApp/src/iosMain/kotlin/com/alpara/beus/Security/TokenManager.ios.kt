package com.alpara.beus.Security

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults

actual class TokenManager {

    private val userDefaults = NSUserDefaults.standardUserDefaults // AlmacenSimple en iOS

    actual suspend fun saveAccessToken(token: String) {
        withContext(Dispatchers.Default) {
            userDefaults.setObject(token, KEY_ACCESS_TOKEN)
            userDefaults.synchronize() // Actualiza el Almacen (equivalente a un commit en bd)
        }
    }

    actual suspend fun getAccessToken(): String? {
        return withContext(Dispatchers.Default) {
            userDefaults.stringForKey(KEY_ACCESS_TOKEN)
        }
    }

    actual suspend fun saveRefreshToken(token: String) {
        withContext(Dispatchers.Default) {
            userDefaults.setObject(token, KEY_REFRESH_TOKEN)
            userDefaults.synchronize()
        }
    }

    actual suspend fun getRefreshToken(): String? {
        return withContext(Dispatchers.Default) {
            userDefaults.stringForKey(KEY_REFRESH_TOKEN)
        }
    }

    actual suspend fun clearTokens() {
        withContext(Dispatchers.Default) {
            userDefaults.removeObjectForKey(KEY_ACCESS_TOKEN)
            userDefaults.removeObjectForKey(KEY_REFRESH_TOKEN)
            userDefaults.synchronize()
        }
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"

        private var instance: TokenManager? = null

        fun getInstance(): TokenManager {
            if (instance == null) {
                instance = TokenManager()
            }
            return instance!!
        }

        internal fun requireInstance(): TokenManager {
            return instance ?: throw IllegalStateException(
                "TokenManager not initialized. Call TokenManager.getInstance() first."
            )
        }
    }
}

actual fun createTokenManager(): TokenManager {
    return TokenManager.requireInstance()
}
