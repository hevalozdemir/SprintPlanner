package de.labb.database.business


import de.labb.database.PostProjekt
import de.labb.database.Projekt
import de.labb.database.generated.tables.records.ProjekteRecord
import de.labb.database.generated.tables.references.PROJEKTE
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.time.LocalDateTime



object ProjektService {
    private val logger = KotlinLogging.logger {}

    private fun insertProjekt(dsl: DSLContext) {
        val projektNamen = listOf("Porto", "Captis", "Bills")
        val neuProjekt = projektNamen.map {
            ProjekteRecord().apply {
                projektename = it
                erstelltam = LocalDateTime.of(2018, 5, 5, 9, 30)
                aktualisiertam = LocalDateTime.now()
            }
        }
        dsl.batchInsert(neuProjekt).execute()
    }

    private fun holeProjekt(dsl: DSLContext): List<Projekt> {
        val result = dsl.fetch("SELECT * FROM projekte ORDER BY projekte.id")
        println(result)
        return dsl.select()
            .from(PROJEKTE)
            .where(DSL.trueCondition())
            .orderBy(PROJEKTE.ID.asc())
            .fetch {
                Projekt(
                    name = it[PROJEKTE.PROJEKTENAME]!!,
                    id = it[PROJEKTE.ID]!!,
                    erstelltAm = it[PROJEKTE.ERSTELLTAM]!!,
                    aktualisiertAm = it[PROJEKTE.AKTUALISIERTAM]!!
                )
            }
    }

    private fun createNeuProjekt(projekt: PostProjekt, dsl: DSLContext): Projekt? {
        return dsl.insertInto(PROJEKTE)
            .set(PROJEKTE.PROJEKTENAME, projekt.name)
            .set(PROJEKTE.ERSTELLTAM, projekt.erstelltAm)
            .set(PROJEKTE.AKTUALISIERTAM, projekt.aktualisiertAm)
            .returning().fetchOne() {
                Projekt(
                    id = it[PROJEKTE.ID]!!,
                    name = it[PROJEKTE.PROJEKTENAME]!!,
                    erstelltAm = it[PROJEKTE.ERSTELLTAM]!!,
                    aktualisiertAm = it[PROJEKTE.AKTUALISIERTAM]!!
                )
            }
    }

    private fun deleteProjekt(id: Long, dsl: DSLContext): Boolean {
        return try {
            val result = dsl.deleteFrom(PROJEKTE)
                .where(PROJEKTE.ID.eq(id))
                .execute()
            result == 1
        } catch (e: Exception) {
            logger.error { "etwas ist schief gelaufen. $e" }
            false
        }
    }

    private fun holeProjektMitId(id: Long, dsl: DSLContext): Projekt? {
        return dsl.select()
            .from(PROJEKTE)
            .where(PROJEKTE.ID.eq(id))
            .fetch {
                Projekt(
                    id = it[PROJEKTE.ID]!!,
                    name = it[PROJEKTE.PROJEKTENAME]!!,
                    erstelltAm = it[PROJEKTE.ERSTELLTAM]!!,
                    aktualisiertAm = it[PROJEKTE.AKTUALISIERTAM]!!
                )
            }.firstOrNull()
    }

    private fun changeProjektName(id: Long, projekt: Projekt, dsl: DSLContext): Boolean {
        return try {
            val result = dsl.update(PROJEKTE)
                .set(PROJEKTE.PROJEKTENAME, projekt.name)
                .where(PROJEKTE.ID.eq(id))
                .execute()
            result == 1
        } catch (e: Exception) {
            false
        }
    }


    fun Route.projektRouten(dsl: DSLContext) {
        get("/projekt/initaleDaten") {
            logger.info { "initale Daten" }
            ProjektService.insertProjekt(dsl)
            call.respond("Hat geklappt")
        }

        get("/projekt") {
            logger.info { "get alle Projekt" }
            val result = ProjektService.holeProjekt(dsl)
            call.respond(result)
        }
        post("/projekt/anlegen") {
            logger.info { "Projekt anlegen" }
            val projekt = call.receive<PostProjekt>()
            val success = ProjektService.createNeuProjekt(projekt, dsl)
            if (success != null) {
                call.respond(success)
            } else {
                call.respondText("kein neues Projekt erstellt!")
            }
        }
        delete("/projekt/delete/{id}") {
            logger.info { "delete Projekt" }
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode(401, "Ungültige Projekt-Id!"), "Ungültige Projekt-Id!")
                return@delete
            }
            val success = ProjektService.deleteProjekt(id, dsl)
            if (success) {
                call.respond("Das Projekt wurde erfolgreich gelöscht!")
            } else {
                call.respond(HttpStatusCode(404, "Das Projekt nicht gefunden!"), "Das Projekt nicht gefunden!")
            }
        }
        get("/projekt/{id}") {
            val id = call.parameters["id"]!!.toLongOrNull()
            logger.info { "get Projekt bei $id" }
            if (id == null) {
                call.respond(HttpStatusCode(401, "Ungültige ID"), "Ungültige ID")
                return@get
            }
            val success = ProjektService.holeProjektMitId(id, dsl)
            if (success == null) {
                call.respond(HttpStatusCode(404, "Projekt nicht gefunden!"), "Projekt nicht gefunden!")
                return@get
            } else {
                call.respond(success)
            }
        }

        put("/projekt/changename/{id}") {
            logger.info { "put projekt changename" }
            val id = call.parameters["id"]!!.toLongOrNull()
            val projekt = call.receive<Projekt>()
            if (id == null) {
                call.respond(HttpStatusCode(401, "Ungültige ID"), "Ungültige ID")
                return@put
            }
            val success = ProjektService.changeProjektName(id, projekt, dsl)
            if (success) {
                logger.info { "Projektname wurde erfolgreich geändert!" }
                call.respondText("Projektname wurde erfolgreich geändert!")
            } else {
                call.respond(HttpStatusCode(404, "Das Projekt nicht gefunden!"), "Das Projekt nicht gefunden!")
            }
        }
    }

}