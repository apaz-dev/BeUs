package com.alpara.beus.Security

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
import platform.darwin.noErr

@OptIn(ExperimentalForeignApi::class)
actual class TokenManager {
    actual suspend fun saveAccessToken(token: String) {
        saveToKeychain(KEY_ACCESS_TOKEN, token)
    }

    actual suspend fun getAccessToken(): String? {
        return getFromKeychain(KEY_ACCESS_TOKEN)
    }

    actual suspend fun saveRefreshToken(token: String) {
        saveToKeychain(KEY_REFRESH_TOKEN, token)
    }

    actual suspend fun getRefreshToken(): String? {
        return getFromKeychain(KEY_REFRESH_TOKEN)
    }

    actual suspend fun clearTokens() {
        deleteFromKeychain(KEY_ACCESS_TOKEN)
        deleteFromKeychain(KEY_REFRESH_TOKEN)
    }

    private fun saveToKeychain(key: String, value: String) {
        // First, try to delete existing value
        deleteFromKeychain(key)

        // Create query dictionary using NSMutableDictionary for easier bridging
        val query = NSMutableDictionary()
        query[kSecClass as Any] = kSecClassGenericPassword as Any
        query[kSecAttrAccount as Any] = key.toNSString()
        query[kSecAttrService as Any] = SERVICE_NAME.toNSString()
        query[kSecValueData as Any] = value.toNSData()
        query[kSecAttrAccessible as Any] = kSecAttrAccessibleWhenUnlockedThisDeviceOnly as Any

        val status = SecItemAdd(query as CFDictionaryRef, null)
        if (status != noErr) {
            println("Failed to save to keychain for key '$key'. Error code: $status (see SecBase.h for error definitions)")
        }
    }

    private fun getFromKeychain(key: String): String? {
        memScoped {
            val query = NSMutableDictionary()
            query[kSecClass as Any] = kSecClassGenericPassword as Any
            query[kSecAttrAccount as Any] = key.toNSString()
            query[kSecAttrService as Any] = SERVICE_NAME.toNSString()
            query[kSecReturnData as Any] = kCFBooleanTrue as Any
            query[kSecMatchLimit as Any] = kSecMatchLimitOne as Any

            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query as CFDictionaryRef, result.ptr)

            if (status == noErr) {
                val data = result.value as? NSData
                return try {
                    data?.toByteArray()?.decodeToString()
                } catch (e: Exception) {
                    println("Failed to decode keychain data for key $key: ${e.message}")
                    null
                }
            }
            return null
        }
    }

    private fun deleteFromKeychain(key: String) {
        val query = NSMutableDictionary()
        query[kSecClass as Any] = kSecClassGenericPassword as Any
        query[kSecAttrAccount as Any] = key.toNSString()
        query[kSecAttrService as Any] = SERVICE_NAME.toNSString()

        SecItemDelete(query as CFDictionaryRef)
    }

    private fun String.toNSString(): NSString {
        return NSString.create(string = this)
    }

    private fun String.toNSData(): NSData {
        return this.encodeToByteArray().toNSData()
    }

    private fun ByteArray.toNSData(): NSData {
        return memScoped {
            NSData.create(bytes = allocArrayOf(this@toNSData), length = this@toNSData.size.toULong())
        }
    }

    private fun NSData.toByteArray(): ByteArray {
        return ByteArray(this.length.toInt()).apply {
            usePinned {
                memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
            }
        }
    }

    companion object {
        private const val SERVICE_NAME = "com.alpara.beus"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        
        private val instance by lazy { TokenManager() }
        
        internal fun getInstance() = instance
    }
}

actual fun createTokenManager(): TokenManager {
    return TokenManager.getInstance()
}
