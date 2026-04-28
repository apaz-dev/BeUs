package com.alpara.beus.Firebase.Auth

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

actual suspend fun getPlatformFcmToken(): String? {
    return try {
        FirebaseMessaging.getInstance().token.await()
    } catch (e: Exception) {
        null
    }
}

