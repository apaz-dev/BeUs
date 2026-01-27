package com.alpara.beus.Security

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
import platform.darwin.noErr
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual class TokenManager {
    actual suspend fun saveAccessToken(token: String) = saveToKeychain(KEY_ACCESS_TOKEN, token)
    actual suspend fun getAccessToken(): String? = getFromKeychain(KEY_ACCESS_TOKEN)
    actual suspend fun saveRefreshToken(token: String) = saveToKeychain(KEY_REFRESH_TOKEN, token)
    actual suspend fun getRefreshToken(): String? = getFromKeychain(KEY_REFRESH_TOKEN)

    actual suspend fun clearTokens() {
        deleteFromKeychain(KEY_ACCESS_TOKEN)
        deleteFromKeychain(KEY_REFRESH_TOKEN)
    }

    private fun saveToKeychain(key: String, value: String) {
        deleteFromKeychain(key)

        val query = baseQuery(key).apply {
            this[kSecValueData] = value.toNSData() as CFTypeRef?
            this[kSecAttrAccessible] = kSecAttrAccessibleWhenUnlockedThisDeviceOnly as CFTypeRef?
        }

        val status = SecItemAdd(query.toCFDictionary(), null)
        if (status != noErr.toInt()) {
            println("Keychain save failed for '$key': $status")
        }
    }

    private fun getFromKeychain(key: String): String? = memScoped {
        val query = baseQuery(key).apply {
            this[kSecReturnData] = kCFBooleanTrue as CFTypeRef?
            this[kSecMatchLimit] = kSecMatchLimitOne as CFTypeRef?
        }

        val result = alloc<CFTypeRefVar>()
        if (SecItemCopyMatching(query.toCFDictionary(), result.ptr) == noErr.toInt()) {
            (result.value as? NSData)?.toByteArray()?.decodeToString()
        } else null
    }

    private fun deleteFromKeychain(key: String) {
        SecItemDelete(baseQuery(key).toCFDictionary())
    }

    private fun baseQuery(key: String) = mutableMapOf<CFStringRef?, CFTypeRef?>(
        kSecClass to kSecClassGenericPassword as CFTypeRef?,
        kSecAttrAccount to key.toNSString() as CFTypeRef?,
        kSecAttrService to SERVICE_NAME.toNSString() as CFTypeRef?
    )

    private fun String.toNSString(): NSString = NSString.create(string = this)

    private fun String.toNSData(): NSData = encodeToByteArray().toNSData()

    private fun ByteArray.toNSData(): NSData = memScoped {
        NSData.create(bytes = allocArrayOf(this@toNSData), length = size.toULong())
    }

    private fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).apply {
        usePinned { memcpy(it.addressOf(0), bytes, length) }
    }

    private fun Map<CFStringRef?, CFTypeRef?>.toCFDictionary(): CFDictionaryRef? =
        CFDictionaryCreateMutable(null, size.toLong(), null, null).also { dict ->
            forEach { (key, value) -> CFDictionaryAddValue(dict, key, value) }
        }

    companion object {
        private const val SERVICE_NAME = "com.alpara.beus"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private val instance by lazy { TokenManager() }
        internal fun getInstance() = instance
    }
}

actual fun createTokenManager() = TokenManager.getInstance()