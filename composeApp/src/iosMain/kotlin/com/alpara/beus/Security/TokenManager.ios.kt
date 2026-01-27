package com.alpara.beus.Security

import kotlinx.cinterop.*
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

        // Create query dictionary
        val query = mutableMapOf<CFStringRef?, CFTypeRef?>()
        query[kSecClass] = kSecClassGenericPassword
        query[kSecAttrAccount] = key.toNSString()
        query[kSecAttrService] = SERVICE_NAME.toNSString()
        query[kSecValueData] = value.toNSData()
        query[kSecAttrAccessible] = kSecAttrAccessibleWhenUnlockedThisDeviceOnly

        val status = SecItemAdd(query.toCFDictionary(), null)
        if (status != noErr) {
            println("Failed to save to keychain: $status")
        }
    }

    private fun getFromKeychain(key: String): String? {
        memScoped {
            val query = mutableMapOf<CFStringRef?, CFTypeRef?>()
            query[kSecClass] = kSecClassGenericPassword
            query[kSecAttrAccount] = key.toNSString()
            query[kSecAttrService] = SERVICE_NAME.toNSString()
            query[kSecReturnData] = kCFBooleanTrue
            query[kSecMatchLimit] = kSecMatchLimitOne

            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query.toCFDictionary(), result.ptr)

            if (status == noErr) {
                val data = result.value as? NSData
                return data?.toByteArray()?.decodeToString()
            }
            return null
        }
    }

    private fun deleteFromKeychain(key: String) {
        val query = mutableMapOf<CFStringRef?, CFTypeRef?>()
        query[kSecClass] = kSecClassGenericPassword
        query[kSecAttrAccount] = key.toNSString()
        query[kSecAttrService] = SERVICE_NAME.toNSString()

        SecItemDelete(query.toCFDictionary())
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

    private fun Map<CFStringRef?, CFTypeRef?>.toCFDictionary(): CFDictionaryRef? {
        return CFDictionaryCreateMutable(null, this.size.toLong(), null, null).also { dict ->
            this.forEach { (key, value) ->
                CFDictionaryAddValue(dict, key, value)
            }
        }
    }

    companion object {
        private const val SERVICE_NAME = "com.alpara.beus"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        
        private val instance = TokenManager()
    }
}

actual fun createTokenManager(): TokenManager {
    return TokenManager.instance
}
