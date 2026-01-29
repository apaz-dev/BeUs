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

@Serializable
data class ProfilePublic(
    val username: String,
    val avatar_url: String
)

@Serializable
data class ProfilePrivate(
    val username: String,
    val email: String,
    val avatar_url: String
)

@Serializable
data class TeamResponse(
    val name: String,
    val join_code: String
)

@Serializable
data class TeamMembersResponse(
    val name: String,
    val members: List<ProfilePublic>
)

@Serializable
data class TeamJoinRequest(
    val join_code: String
)

@Serializable
data class TeamCreateRequest(
    val name: String,
    val owner_id: Int
)