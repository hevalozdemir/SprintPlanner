package de.labb.database

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Projekt (
    val id: Long,
    val name: String,
    @Contextual
    val erstelltAm: LocalDateTime?=null,
    @Contextual
    val aktualisiertAm: LocalDateTime?=null
)

@Serializable
data class PostProjekt(
    val name: String,
    @Contextual
    val erstelltAm: LocalDateTime?=null,
    @Contextual
    val aktualisiertAm: LocalDateTime?=null
)