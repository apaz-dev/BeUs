package com.alpara.beus.Security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class TokenManager(private val context: Context) {

    //Clave maestra para cifrar en las "SharedPreferences"
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()


    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME, // Nombre del baul seguro
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    actual suspend fun saveAccessToken(token: String) {
        // Usar el un hilo especifico para operaciones de IO
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
        }
    }

    actual suspend fun getAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        }
    }

    actual suspend fun saveRefreshToken(token: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
        }
    }

    actual suspend fun getRefreshToken(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
        }
    }

    actual suspend fun clearTokens() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "beus_secure_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"

        @Volatile
        private var instance: TokenManager? = null

        // Si existe devuelve la instancia, si no la crea de forma segura
        fun getInstance(context: Context): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager(context.applicationContext).also { instance = it }
            }
        }

        internal fun requireInstance(): TokenManager {
            return instance ?: throw IllegalStateException(
                "TokenManager not initialized. Call TokenManager.getInstance(context) first."
            )
        }
    }
}

actual fun createTokenManager(): TokenManager {
    return TokenManager.requireInstance()
}