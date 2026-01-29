package com.alpara.beus

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform