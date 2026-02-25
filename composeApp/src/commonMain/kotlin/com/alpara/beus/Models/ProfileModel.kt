package com.alpara.beus.Models

import kotlinx.serialization.Serializable

@Serializable
data class ProfilePublic(
    val username: String,
    val avatar_url: String
)

@Serializable
data class ProfileTeam(
    val name: String,
    val join_code: String,
    val team_id: String = ""
)

@Serializable
data class ProfilePrivate(
    val username: String,
    val email: String,
    val avatar_url: String,
    // Lista de equipos a los que pertenece el usuario
    val teams: List<ProfileTeam>
)