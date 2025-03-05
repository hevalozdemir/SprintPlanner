package de.labb.database

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Plan(
    val id :Long,
    val benutzer: Long,
    val projekt: Long,
    val stunde: Int,
    @Contextual
    val planvon: LocalDate?=null,
    @Contextual
    val planbis: LocalDate?=null,
    @Contextual
    val erstelltAm: LocalDateTime?=null,
    @Contextual
    val aktualisiertAm: LocalDateTime?=null,
    val erstelltVon: Long,
    val aktualisiertVon: Long
)

@Serializable
data class PutPlan(
    val id :Long,
    val benutzer: Long,
    val projekt: Long,
    val stunde: Int,
    @Contextual
    val planvon: LocalDate?=null,
    @Contextual
    val planbis: LocalDate?=null,
    @Contextual
    val erstelltAm: LocalDateTime?=null,
    @Contextual
    val aktualisiertAm: LocalDateTime?=null,
    val erstelltVon: Long,
    val aktualisiertVon: Long
)

@Serializable
data class PostPlan(
    val benutzer: Long,
    val projekt: Long,
    val stunde: Int,
    @Contextual
    val planvon: LocalDate?=null,
    @Contextual
    val planbis: LocalDate?=null,
    @Contextual
    val erstelltAm: LocalDateTime?=null,
    @Contextual
    val aktualisiertAm: LocalDateTime?=null,
    val erstelltVon: Long,
    val aktualisiertVon: Long

)