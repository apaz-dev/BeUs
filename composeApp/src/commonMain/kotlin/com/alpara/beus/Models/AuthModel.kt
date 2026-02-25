package com.alpara.beus.Models

import kotlinx.serialization.Serializable

/*
    @Serializable -> Se usa para decirle al compilador que tiene que ser serializado (convertido a JSON)

    Es decir, tu le dices a la  "api" que has iniciado sesion y le necesitas pasas un json peri tienes estas vairables

    username = pepito
    contraseña = "CONTRASEA"

    pues lo conviertes a:

    {name: "pepito", password: "CONTRASEÑA"}

    y la api si es correcto te da 2 cosas:

    {accessToken: "TOKEN", refreshToken: "TOKEN"} o tmbm puedes tener:

    {accessToken: "TOKEN", refreshToken: "TOKEN", tokenType: "Bearer"}

    Y ahora a la inversa seria deserializarlo y convertirlo en variables q kmp entiendas
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