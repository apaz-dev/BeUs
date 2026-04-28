package com.alpara.beus.Firebase.Auth

actual suspend fun getPlatformFcmToken(): String? {
    // iOS FCM token is managed by the AppDelegate / SwiftUI lifecycle.
    // Tokens should be registered from the iOS side via
    // Messaging.messaging().token and written directly to Firestore.
    return null
}

