package de.labb.database

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime


@Serializable
data class View(
    val benutzer: Long,
    @Contextual
    val planVon: LocalDate?= null,
    @Contextual
    val planBis: LocalDate?= null,
    val stunde :Int,
    @Contextual
    val datum :LocalDate?=null
)

@Serializable
data class RestPlanView(
    val benutzername: String,
    val projektname : String,
    val benutzer: Long,
    val projekt: Long,
    val stunde :Int,
    @Contextual
    val planvon: LocalDate? = null,
    @Contextual
    val planbis: LocalDate? = null,
    @Contextual
    val erstelltAm: LocalDateTime? = null,
    val erstelltVon : Long,
    @Contextual
    val aktualisiertAm: LocalDateTime? =null,
    val aktualisiertVon: Long
)

@Serializable
data class RestPlanZeitView(
    val projekt: Long,
    val totalstunde :Long,
    @Contextual
    val planvon: LocalDate? = null,
)
