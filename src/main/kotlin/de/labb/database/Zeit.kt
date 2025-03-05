package de.labb.database

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Zeit (
    val id: Long,
    val benutzer: Long,
    val projekt: Long,
    val stunde: Int,
    @Contextual
    val erstelltAm: LocalDateTime?=null,
    @Contextual
    val aktualisiertAm: LocalDateTime? =null,
    @Contextual
    val datum :LocalDate
)
@Serializable
data class GetZeit (
    val id : Long,
    val benutzer: Long,
    val projekt: Long,
    val stunde: Int,
    @Contextual
    val erstelltAm: LocalDateTime?=null,
    @Contextual
    val aktualisiertAm: LocalDateTime?=null,
    @Contextual
    val datum :LocalDate?= null
)
@Serializable
data class PostZeit(
    val benutzer: Long,
    val projekt: Long,
    val stunde: Int,
    @Contextual
    val erstelltAm: LocalDateTime?=null,
    @Contextual
    val aktualisiertAm: LocalDateTime?=null,
    @Contextual
    val datum :LocalDate?=null
)