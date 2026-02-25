package com.alpara.beus.Models

import kotlinx.serialization.Serializable

/*
    @Serializable -> Se usa para decirle al compilador que tiene que ser serializado (convertido a JSON)


    En este caso solo se usa para deserializar (convertir de JSON a variables de KMP) los datos de la api,
    no se serialiaz porq los datos se lo vamos a pedir con GET (fricada moment: GET no acepta o se deberia aceptar datos por el body en la peticion)
*/
@Serializable
data class ProfilePublic(
    val username: String,
    val avatar_url: String
)

@Serializable
data class ProfileTeam(
    val name: String,
    val join_code: String,
    val team_id: String = "",
    val members_count: Int = 0
)

@Serializable
data class ProfilePrivate(
    val username: String,
    val email: String,
    val avatar_url: String,
    // Lista de equipos a los que pertenece el usuario
    val teams: List<ProfileTeam>
)