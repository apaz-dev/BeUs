package com.alpara.beus.Models

import kotlinx.serialization.Serializable

/*
    @Serializable -> Se usa para decirle al compilador que tiene que ser serializado (convertido a JSON)
*/

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String? = null
)

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
)

@Serializable
data class RegisterResponse(
    val username: String
)
