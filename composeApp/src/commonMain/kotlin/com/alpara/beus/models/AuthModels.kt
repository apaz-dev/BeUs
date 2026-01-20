package com.alpara.beus.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val refresh_token: String? = null
)

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val full_name: String? = null
)

@Serializable
data class UserResponse(
    val id: Int,
    val email: String,
    val username: String,
    val full_name: String?
)
