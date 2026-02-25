package com.alpara.beus.Firebase.Auth

object FirebaseConfig {
    private var initialized = false
    
    fun initialize() {
        if (!initialized) {
            initializePlatform()
            initialized = true
        }
    }
}

expect fun initializePlatform()
