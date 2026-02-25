package com.alpara.beus.Models


import kotlinx.serialization.Serializable

/*
    @Serializable -> Se usa para decirle al compilador que tiene que ser serializado (convertido a JSON)


    Es decir, tu le dices a la  "api" que has creado un equipo y le necesitas pasas un json peri tienes estas vairables

    name = pepito
    owner_id = OWNER_EQUIPO
    pues lo conviertes a:

    {name: "pepito", owner_id: "OEWNER_EQUIPO"

    y la api si es correcto te da 2 cosas:

    {name: "NOMBREDELEQUIPO", join_code: "123456"} pues kotlin va a coger eso y lo va a almacenar en las

    Y ahora a la inversa seria deserializarlo y convertirlo en variables q kmp entiendas
*/
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

@Serializable
data class TeamCreateResponse(
    val message: String,
    val code: String
)

@Serializable
data class TeamJoinResponse(
    val message: String
)