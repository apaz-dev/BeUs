package com.alpara.beus.Utils

enum class EventType {
    FIESTA,
    BAR,
    MONTANA,
    CENA,
    VIAJE,
    COMPETICION
}

enum class RequirementType {
    VLOG,
    FOTOS,
    ANECDOTAS,
    ENCUESTA,
    CLASIFICACION
}


val eventRequirements: Map<EventType, List<RequirementType>> = mapOf(
    EventType.FIESTA to listOf(
        RequirementType.VLOG,
        RequirementType.FOTOS,
        RequirementType.ANECDOTAS
    ),

    EventType.BAR to listOf(
        RequirementType.FOTOS,
        RequirementType.ENCUESTA
    )
)

