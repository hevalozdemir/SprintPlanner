package de.labb.database.business

import de.labb.database.GetZeit
import de.labb.database.PostZeit
import de.labb.database.Zeit
import de.labb.database.generated.tables.records.ZeitenRecord
import de.labb.database.generated.tables.references.ZEITEN
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.time.LocalDate
import java.time.LocalDateTime

object ZeitenService {
    private val logger = KotlinLogging.logger{}


    private fun insertZeit(dsl: DSLContext){
        val zeitRecordList = mutableListOf<ZeitenRecord>()
        val zeitRecord = ZeitenRecord().apply {
            benutzer = 63
            projekt =73
            stunde =10
            erstelltam = LocalDateTime.of(2023,4,4, 15,30)
            aktualisiertam= LocalDateTime.now()
            datum = LocalDate.of(2024,5,1)
        }
        zeitRecordList.add(zeitRecord)
        dsl.batchInsert(zeitRecordList).execute()
    }
    private  fun holeZeit(dsl: DSLContext) :List<Zeit>{
        return dsl.select()
            .from(ZEITEN)
            .where(DSL.trueCondition())
            .orderBy(ZEITEN.ID.asc())
            .fetch {
                Zeit(
                    id = it[ZEITEN.ID]!!,
                    benutzer = it[ZEITEN.BENUTZER]!!,
                    projekt = it[ZEITEN.PROJEKT]!!,
                    stunde = it[ZEITEN.STUNDE]!!,
                    erstelltAm = it[ZEITEN.ERSTELLTAM]!!,
                    aktualisiertAm = it[ZEITEN.AKTUALISIERTAM]!!,
                    datum = it[ZEITEN.DATUM]!!
                )
            }
    }
    private fun deleteZeit(id: Long, dsl: DSLContext): Boolean {
        return try {
            val result= dsl.deleteFrom(ZEITEN)
                .where(ZEITEN.ID.eq(id))
                .execute()
            result ==1
        }catch (e:Exception){
            false
        }
    }
    private fun changeStunde(id:Long, dsl: DSLContext, zeit: Zeit): Boolean{
        return try {
            val result = dsl.update(ZEITEN)
                .set(ZEITEN.STUNDE, zeit.stunde)
                .where(ZEITEN.ID.eq(id))
                .returning().execute()
            result==1
        }catch (e: Exception) {
            false
        }
    }
    private fun holeZeitMitId (id: Long, dsl: DSLContext) :GetZeit?{
       return dsl.select()
               .from(ZEITEN)
               .where(ZEITEN.ID.eq(id))
               .fetch{
                   GetZeit(
                       id = it[ZEITEN.ID]!!,
                       benutzer = it[ZEITEN.BENUTZER]!!,
                       projekt = it[ZEITEN.PROJEKT]!!,
                       stunde = it[ZEITEN.STUNDE]!!,
                       erstelltAm = it[ZEITEN.ERSTELLTAM]!!,
                       aktualisiertAm = it[ZEITEN.AKTUALISIERTAM]!!,
                       datum = it[ZEITEN.DATUM]!!
                   )
               }.firstOrNull()
    }
    private fun createNewZeit(zeit: PostZeit, dsl: DSLContext) :Zeit?{
        return dsl.insertInto(ZEITEN)
            .set(ZEITEN.BENUTZER, zeit.benutzer)
            .set(ZEITEN.PROJEKT, zeit.projekt)
            .set(ZEITEN.STUNDE, zeit.stunde)
            .set(ZEITEN.ERSTELLTAM, zeit.erstelltAm)
            .set(ZEITEN.AKTUALISIERTAM, zeit.aktualisiertAm)
            .set(ZEITEN.DATUM, zeit.datum)
            .returning().fetchOne {
                Zeit (
                    id = it[ZEITEN.ID]!!,
                    benutzer = it[ZEITEN.BENUTZER]!!,
                    projekt = it[ZEITEN.PROJEKT]!!,
                    stunde = it[ZEITEN.STUNDE]!!,
                    erstelltAm = it[ZEITEN.ERSTELLTAM]!!,
                    aktualisiertAm = it[ZEITEN.AKTUALISIERTAM]!!,
                    datum = it[ZEITEN.DATUM]!!
                )
            }
    }

    fun Route.zeitRouten(dsl: DSLContext){
        get("/zeit/initialDaten"){
            logger.info { "initale Daten" }
            ZeitenService.insertZeit(dsl)
            call.respondText("Zeit wurde erfolgreich erstellt!")
        }
        get("/zeit"){
            logger.info { "get alle Zeit" }
            val zeit = ZeitenService.holeZeit(dsl).sortedBy { it.id }
            call.respond(zeit)
            call.respond("Hat geklappt!")
        }
        delete("/zeit/delete/{id}"){
            val id = call.parameters["id"]?.toLongOrNull()
            if(id== null){
                call.respond(HttpStatusCode(401, "Ungültige Zeit id"))
                return@delete
            }
            val success =ZeitenService.deleteZeit(id, dsl)
            if(success){
                logger.info { "id $id Zeit wurde erfolgreich gelöscht!" }
                call.respond("Zeit wurde erfolgreich gelöscht!")
            } else {
                call.respond(HttpStatusCode(404, "Zeit nicht gefunden!"), "Zeit nicht gefunden!")
            }
        }
        put("/zeit/changeStunde/{id}"){
            logger.info { "Put Zeit bearbeiten" }
            val id = call.parameters["id"]!!.toLongOrNull()
            val zeit =call.receive<Zeit>()
            if (id == null){
                call.respond(HttpStatusCode(401, "Ungültige id"), "Ungültige id")
                return@put
            }
            val success = ZeitenService.changeStunde(id, dsl, zeit)
            if(success){
                logger.info { "ZEIT ID : $id Stunde wurde erfolgreich geändert!" }
                call.respond("Stunde wurde erfolgreich geändert!")
            }else{
                logger.info { "ZEIT ID: $id Stunde wurde nicht geändert!" }
                call.respond(HttpStatusCode(404, "Stunde wurde nicht geändert!"), "Stunde wurde nicht geändert!")
            }
        }
        get("/zeit/{id}"){
            logger.info { "get Zeit bei id" }
            val id = call.parameters["id"]?.toLongOrNull()
            if(id == null){
                call.respond(HttpStatusCode(401, "Null | Ungültige id" ), "Null | Ungültige id")
                return@get
            }
            val result = ZeitenService.holeZeitMitId(id, dsl)
            if(result == null){
                call.respond(HttpStatusCode(404, "Zeit nicht gefunden"), "Zeit nicht gefunden")
                return@get
            }else {
                call.respond(result)
            }

        }
        post("/zeit/anlegen"){
            logger.info { "Create New Zeit" }
            val zeit = call.receive<PostZeit>()
            val success =ZeitenService.createNewZeit(zeit, dsl )
            if (success != null){
                call.respond(success)
            }else {
                call.respondText("Es wurde keine neue Zeit erstellt.")
            }
        }
    }


}