package com.alpara.beus.Models.Profile

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
data class ProfilePrivate(
    val username: String,
    val email: String,
    val avatar_url: String
)