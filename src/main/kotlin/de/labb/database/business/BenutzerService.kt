package de.labb.database.business

import de.labb.database.Benutzer
import de.labb.database.PostBenutzer
import de.labb.database.generated.tables.records.BenutzerRecord
import de.labb.database.generated.tables.references.BENUTZER
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.callbackFlow

import mu.KotlinLogging
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.time.LocalDateTime

object BenutzerService {
    private val logger= KotlinLogging.logger{}


    private fun holeBenutzer(dsl: DSLContext) :List<Benutzer>{
        //val result = dsl.fetch("SELECT * FROM benutzer ORDER BY benutzer.id")
        //println(result)
        return dsl.select()
            .from(BENUTZER)
            .where(DSL.trueCondition())
            .orderBy(BENUTZER.ID.asc())
            .fetch {
                Benutzer(
                    id = it[BENUTZER.ID]!!,
                    name = it[BENUTZER.BENUTZERNAME]!!,
                    erstelltAm = it[BENUTZER.ERSTELLTAM]!!,
                    aktualisiertAm = it[BENUTZER.AKTUALISIERTAM]!!
                )
            }
    }

    private fun insertBenutzer(dsl: DSLContext){
        val benutzerNamen = listOf("Eva Lina", "Tobias", "Max", "Marc", "Alina")
        val neuBenutzer = benutzerNamen.map {
            BenutzerRecord().apply {
                benutzername= it
                erstelltam = LocalDateTime.of(2018,5,20,8,30)
                aktualisiertam = LocalDateTime.now()
            }
        }
        dsl.batchInsert(neuBenutzer).execute()
    }

    private fun deleteBenutzer(id:Long, dsl: DSLContext) :Boolean{
        return try {
            val result = dsl.deleteFrom(BENUTZER)
                .where(BENUTZER.ID.eq(id))
                .execute()
            result ==1
        }
        catch (e: Exception){
            false
        }
    }

    private fun holeBenutzerMitID(id: Long, dsl: DSLContext) :Benutzer? {
        val result = dsl.select()
            .from(BENUTZER)
            .where(BENUTZER.ID.eq(id))
            .fetch {
                Benutzer(
                    id= it[BENUTZER.ID]!!,
                    name = it[BENUTZER.BENUTZERNAME]!!,
                    erstelltAm = it[BENUTZER.ERSTELLTAM]!!,
                    aktualisiertAm = it[BENUTZER.AKTUALISIERTAM]!!

                )
            }.firstOrNull()
        return result
    }
    private fun changeBenutzerName(id:Long, benutzer: Benutzer, dsl: DSLContext) :Boolean {
        return try {
            val result =dsl.update(BENUTZER)
                .set(BENUTZER.BENUTZERNAME, benutzer.name)
                .where(BENUTZER.ID.eq(id))
                .execute()
            result ==1
        }
        catch (e:Exception){
            false
        }
    }
    private fun createNewBenutzer(benutzer: PostBenutzer, dsl: DSLContext) :Benutzer?{
        return dsl.insertInto(BENUTZER)
            .set(BENUTZER.BENUTZERNAME, benutzer.name)
            .set(BENUTZER.ERSTELLTAM, benutzer.erstelltAm)
            .set(BENUTZER.AKTUALISIERTAM, benutzer.aktualisiertAm)
            .returning().fetchOne() {
                Benutzer (
                    id = it[BENUTZER.ID]!!,
                    name = it[BENUTZER.BENUTZERNAME]!!,
                    erstelltAm = it[BENUTZER.ERSTELLTAM]!!,
                    aktualisiertAm = it[BENUTZER.AKTUALISIERTAM]!!
                )
            }
    }

    fun Route.benutzerRouten(dsl: DSLContext){

        get("/benutzer"){
            logger.info{ "initale Daten" }
            val benutzer = BenutzerService.holeBenutzer(dsl)
            call.respond(benutzer)
        }

        get("/benutzer/initialeDaten"){
            logger.info { "initiale Daten" }
            BenutzerService.insertBenutzer(dsl)
            call.respond("Hat geklappt")
        }

        delete("/benutzer/delete/{id}"){
            logger.info { "Delete Benutzer" }
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null){
                logger.info { "Ungültige Benutzer-Id" }
                call.respond(HttpStatusCode(401, "Ungültige Benutzer - Id"), "Ungültige Benutzer-Id")
                return@delete
            }
            val success = BenutzerService.deleteBenutzer(id, dsl)
            if(success){
                call.respond("Der Benutzer wurde erfolgreich gelöscht.")
                logger.info { "Der Benutzer (id: $id) wurde erfolgreich gelöscht." }
            }
            else{
                call.respond(HttpStatusCode(404, "Benutzer nicht gefunden"), "Benutzer nicht gefunden.")
            }
        }

        get("/benutzer/{id}"){
            val id= call.parameters["id"]?.toLongOrNull()
            logger.info { "get Benutzer bei id: $id" }
            if(id == null){
                call.respond(HttpStatusCode(404, "Benutzer ID : Null"))
                return@get
            }
            else {
                val result = BenutzerService.holeBenutzerMitID(id, dsl)
                if(result ==null){
                    call.respond(HttpStatusCode(404, "Benutzer nicht gefunden!"), "Benutzer nicht gefunden!")
                    logger.info { "Benutzer nicht gefunden!" }
                    return@get
                }
                call.respond(result)
            }
        }
        put("benutzer/bearbeiten/{id}"){
            val id = call.parameters["id"]?.toLongOrNull()
            logger.info { "put benutzer bearbeiten $id" }

            val benutzer = call.receive<Benutzer>()

            if(id == null) {
                call.respond(HttpStatusCode(401, "Ungültige Benutzer-Id" ), "Ungültige Benutzer-Id")
                return@put
            }
            val success = BenutzerService.changeBenutzerName(id, benutzer, dsl)

            if(success){
                logger.info { "Benutzername wurde erfolgreich geändert!" }
                call.respondText("Benutzername wurde erfolgreich geändert!")
            }else{
                logger.info { "Das Ändern des Benutzernamens ist fehlgeschlagen!" }
                call.respondText("Das Ändern des Benutzernamens ist fehlgeschlagen!")
            }
        }
        post("/benutzer/anlegen"){
            logger.info { "post benutzer anlegen" }
            val benutzer = call.receive<PostBenutzer>()
            val success = BenutzerService.createNewBenutzer(benutzer, dsl)
            if(success != null){
                call.respond("Neuer Benutzer erfolgreich erstellt!")
                call.respond(success)
            }else {
                call.respond("Der neu Benutzer nicht erstellt werden!")
            }
        }
    }
}