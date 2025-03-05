package de.labb.database.business
import de.labb.database.Plan
import de.labb.database.PostPlan
import de.labb.database.generated.tables.records.BenutzerRecord
import de.labb.database.generated.tables.records.PlanRecord
import de.labb.database.generated.tables.records.ProjekteRecord
import de.labb.database.generated.tables.records.ZeitenRecord
import de.labb.database.generated.tables.references.BENUTZER
import de.labb.database.generated.tables.references.PLAN
import de.labb.database.generated.tables.references.PROJEKTE
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

object PlanService {
    val logger = KotlinLogging.logger {}
    private fun holePlan(dsl: DSLContext): List<Plan> {
        return dsl.select()
            .from(PLAN)
            .where(DSL.trueCondition())
            .orderBy(PLAN.ID.asc())
            .fetch {
                Plan(
                    id = it[PLAN.ID]!!,
                    benutzer = it[PLAN.BENUTZER]!!,
                    projekt = it[PLAN.PROJEKT]!!,
                    stunde = it[PLAN.STUNDE]!!,
                    planvon = it[PLAN.PLAN_VON]!!,
                    planbis = it[PLAN.PLAN_BIS]!!,
                    erstelltAm = it[PLAN.ERSTELLTAM]!!,
                    erstelltVon = it[PLAN.ERSTELLTVON]!!,
                    aktualisiertAm = it[PLAN.AKTUALISIERTAM]!!,
                    aktualisiertVon = it[PLAN.AKTUALISIERTVON]!!
                )
            }
    }

    private fun insertPlan(dsl: DSLContext) {
        val planRecordList = mutableListOf<PlanRecord>()
        val planRecord = PlanRecord().apply {
            benutzer = 63
            projekt = 73
            stunde = 15
            planVon = LocalDate.of(2024, 4, 1)
            planBis = LocalDate.of(2024, 5, 3)
            erstelltam = LocalDateTime.of(2018, 5, 20, 8, 30)
            erstelltvon = 30
            aktualisiertam = LocalDateTime.now()
            aktualisiertvon = 30
        }
        planRecordList.add(planRecord)
        dsl.batchInsert(planRecordList).execute()
    }

    private fun deletePlan(id: Long, dsl: DSLContext): Boolean {
        return try {
            val result = dsl.deleteFrom(PLAN)
                .where(PLAN.ID.eq(id))
                .execute()
            result == 1
        } catch (e: Exception) {
            false
        }
    }

    private fun changeStunde(id: Long, plan: Plan, dsl: DSLContext): Boolean {
        return try {
            val result = dsl.update(PLAN)
                .set(PLAN.STUNDE, plan.stunde)
                .where(PLAN.ID.eq(id))
                .execute()
            result == 1
        } catch (e: Exception) {
            false
        }
    }

    private fun holePlanMitId(id: Long, dsl: DSLContext): Plan? {
        return dsl.select()
            .from(PLAN)
            .where(PLAN.ID.eq(id))
            .fetch {
                Plan(
                    id = it[PLAN.ID]!!,
                    benutzer = it[PLAN.BENUTZER]!!,
                    projekt = it[PLAN.PROJEKT]!!,
                    stunde = it[PLAN.STUNDE]!!,
                    planvon = it[PLAN.PLAN_VON]!!,
                    planbis = it[PLAN.PLAN_BIS]!!,
                    erstelltAm = it[PLAN.ERSTELLTAM]!!,
                    erstelltVon = it[PLAN.ERSTELLTVON]!!,
                    aktualisiertAm = it[PLAN.AKTUALISIERTAM]!!,
                    aktualisiertVon = it[PLAN.AKTUALISIERTVON]!!
                )
            }.firstOrNull()
    }

    private fun createNewPlan(plan: PostPlan, dsl: DSLContext): Plan? {
        try {
            return dsl.insertInto(PLAN)
                .set(PLAN.BENUTZER, plan.benutzer)
                .set(PLAN.PROJEKT, plan.projekt)
                .set(PLAN.PLAN_BIS, plan.planbis)
                .set(PLAN.PLAN_VON, plan.planvon)
                .set(PLAN.STUNDE, plan.stunde)
                .set(PLAN.ERSTELLTAM, plan.erstelltAm)
                .set(PLAN.ERSTELLTVON, plan.erstelltVon)
                .set(PLAN.AKTUALISIERTAM, plan.aktualisiertAm)
                .set(PLAN.AKTUALISIERTVON, plan.aktualisiertVon)
                .returning().fetchOne() {
                    Plan(
                        id = it[PLAN.ID]!!,
                        benutzer = it[PLAN.BENUTZER]!!,
                        projekt = it[PLAN.PROJEKT]!!,
                        stunde = it[PLAN.STUNDE]!!,
                        planvon = it[PLAN.PLAN_VON]!!,
                        planbis = it[PLAN.PLAN_BIS]!!,
                        erstelltAm = it[PLAN.ERSTELLTAM]!!,
                        erstelltVon = it[PLAN.ERSTELLTVON]!!,
                        aktualisiertAm = it[PLAN.AKTUALISIERTAM]!!,
                        aktualisiertVon = it[PLAN.AKTUALISIERTVON]!!
                    )
                }
        } catch (e: Exception) {
            logger.error("", e)
            return null
        }
    }
    private fun loeschePlan(dsl: DSLContext) {
        dsl.deleteFrom(PLAN).execute()
    }

    private fun loescheProjekte(dsl: DSLContext) {
        dsl.deleteFrom(PROJEKTE).execute()
    }

    private fun loescheBenutzer(dsl: DSLContext) {
        dsl.deleteFrom(BENUTZER).where(BENUTZER.ID.ne(30)).execute()

    }

    private fun loescheZeiten(dsl: DSLContext) {
        dsl.deleteFrom(ZEITEN).execute()

    }
    private fun insertPlanZwei(dsl: DSLContext){
        loeschePlan(dsl)
        loescheZeiten(dsl)
        loescheBenutzer(dsl)
        loescheProjekte(dsl)
        val stundelist = listOf(8, 12, 15, 5)
        val planRecordList = mutableListOf<PlanRecord>()

        val projektNamen = listOf("Weiss", "Schwarz", "Rot", "Gelb", "Blau", "Grün", "Violett", "Braun", "Grau", "Orange")
        val neueProjekte = projektNamen.map {
            ProjekteRecord().apply {
                projektename = it
                erstelltam = LocalDateTime.of(2024,7,7,12,30)
                aktualisiertam=LocalDateTime.now()
            }
        }
        dsl.batchInsert(neueProjekte).execute()
        val projekte = dsl.select().from(PROJEKTE).fetchInto(ProjekteRecord::class.java)


        val benutzerNamen =
            listOf("Alina", "Benjamin", "Marc", "Max", "Tobias", "Lars", "Finn", "Frederik", "Andrea", "Anita")
        benutzerNamen.map {
            val benutzerRecord = BenutzerRecord().apply {
                benutzername = it
                erstelltam=LocalDateTime.of(2024,7,7,15,30)
                aktualisiertam=LocalDateTime.now()
            }
            val neuerBenutzer =
                dsl.insertInto(BENUTZER).set(benutzerRecord).returning().fetchInto(BenutzerRecord::class.java).first()



            repeat(4) { week ->
                val startDatum = LocalDate.of(2024, 3, 25)
                val plan_von = startDatum.plusWeeks(week.toLong())  // Hafta başlangıç tarihi
                val plan_bis = plan_von.plusDays(4) // Hafta bitiş tarihi (5 günlük hafta)

                repeat(4) {
                    val planRecord = PlanRecord().apply {
                        benutzer = neuerBenutzer.id
                        projekt = projekte.random().id
                        stunde = 10 //40:4=10
                        planVon = plan_von
                        planBis = plan_bis
                        erstelltam = LocalDateTime.of(2018, 5, 12, 15, 30)
                        erstelltvon = 30
                        aktualisiertam = LocalDateTime.now()
                        aktualisiertvon = 30
                    }
                    planRecordList.add(planRecord)

                }


            }

            val neuStunde = stundelist.map {
                ZeitenRecord().apply {
                    stunde = it
                    datum = LocalDate.of(2024, 3, 26)
                    benutzer = neuerBenutzer.id
                    projekt = projekte.random().id
                    erstelltam =LocalDateTime.of(2024,7,7,12,30)
                    aktualisiertam=LocalDateTime.now()
                }
            }
            dsl.batchInsert(neuStunde).execute()
        }

        dsl.batchInsert(planRecordList).execute()

    }

        fun Route.planRouten(dsl: DSLContext) {
            get("/plan") {
                logger.info { "get alle Plan" }
                val result = PlanService.holePlan(dsl)
                call.respond(result)

            }
            get("/plan/initialeDaten") {
                logger.info { "initiale Daten" }
                PlanService.insertPlan(dsl)
                call.respond("Hat geklappt!")
            }
            delete("/plan/delete/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode(401, "Null | Ungültige Plan-Id"), "Ungültige Plan-Id")
                    return@delete
                }
                val success = PlanService.deletePlan(id, dsl)
                if (success) {
                    call.respond("Der Plan wurde erfolgreich gelöscht!")
                } else {
                    call.respond(HttpStatusCode(404, "Plan nicht gefunden!"), "Plan nicht gefunden!")
                }
            }
            put("/plan/changeStunde/{id}") {
                logger.info { "Change Stunde!" }
                val id = call.parameters["id"]!!.toLongOrNull()
                val plan = call.receive<Plan>()
                if (id == null) {
                    call.respond(HttpStatusCode(401, "Ungültige Plan-Id"), "Ungültige Plan-Id")
                    return@put
                }
                val success = PlanService.changeStunde(id, plan, dsl)
                if (success) {
                    logger.info { "Stunde wurde erfolgreich geändert!" }
                    call.respond("Stunde wurde erfolgreich geändert!")
                } else {
                    logger.info { "Stunde wurde nicht geändert!" }
                    call.respond(HttpStatusCode(404, "Stunde wurde nicht geändert!"), "Stunde wurde nicht geändert!")
                }
            }
            get("/plan/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                logger.info { "Hole Plan bei id: $id" }
                if (id == null) {
                    call.respond(HttpStatusCode(401, "Ungültige Plan-id"), "Ungültige Plan-Id")
                    return@get
                }
                val success = PlanService.holePlanMitId(id, dsl)
                if (success == null) {
                    logger.info { "Plan nicht gefunden!" }
                    call.respond(HttpStatusCode(404, "Plan nicht gefunden!"), "Plan nicht gefunden!")
                } else {
                    call.respond(success)
                }
            }
            post("/plan/anlegen") {
                logger.info { "POST/plan anlegen" }
                try {
                    val plan = call.receive<PostPlan>()
                    val success = PlanService.createNewPlan(plan, dsl)
                    if (success != null) {
                        logger.info { "Der neue Plan wurde erfolgreich erstellt." }
                        call.respond(success)
                    } else {
                        logger.info { "Neue Plan nicht erstellt werden!" }
                        call.respond(HttpStatusCode.BadRequest, "Neue Plan nicht erstellt werden!")
                    }
                } catch (e: Exception) {
                    logger.error("", e)
                }
            }
            get("/plan/initaleDaten2") {
                logger.info { "initale Daten -2" }
                PlanService.insertPlanZwei(dsl)
                call.respond("Hat geklappt")
            }


        }
    }