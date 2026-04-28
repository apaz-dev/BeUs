package com.alpara.beus.Utils

enum class EventType {
    FIESTA,
    BAR,
    MONTANA,
    CENA,
    VIAJE,
    COMPETICION
}

enum class EventRole(val displayName: String, val emoji: String) {
    POLICIA("Policía", "🚔"),
    VLOGGER("Vlogger", "🎥"),
    CONTADOR("Contador", "📊")
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

