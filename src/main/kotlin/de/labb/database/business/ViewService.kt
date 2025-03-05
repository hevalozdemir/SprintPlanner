package de.labb.database.business

import de.labb.database.RestPlanView
import de.labb.database.RestPlanZeitView
import de.labb.database.View
import de.labb.database.generated.tables.references.ANZEIGE_VIEW
import de.labb.database.generated.tables.references.PLANZEIT_VIEW
import de.labb.database.generated.tables.references.PLAN_VIEW
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.callbackFlow
import mu.KotlinLogging
import org.jooq.DSLContext
import org.jooq.impl.DSL

object ViewService {

    private val logger=KotlinLogging.logger { }

    private fun holeView(dsl:DSLContext):List<View>{
        return dsl.select()
            .from(ANZEIGE_VIEW)
            .where(DSL.trueCondition())
            .fetch {
                View(
                    benutzer = it[ANZEIGE_VIEW.BENUTZER]!!,
                    planVon = it[ANZEIGE_VIEW.PLAN_VON]!!,
                    planBis = it[ANZEIGE_VIEW.PLAN_BIS]!!,
                    stunde = it[ANZEIGE_VIEW.STUNDE]!!,
                    datum = it[ANZEIGE_VIEW.DATUM]!!
                )
            }
    }

    private fun holePlanView(dsl: DSLContext) :List<RestPlanView>{
        return dsl.select()
            .from(PLAN_VIEW)
            .fetch {
                RestPlanView(
                    benutzername = it[PLAN_VIEW.BENUTZERNAME]!!,
                    benutzer = it[PLAN_VIEW.BENUTZER]!!,
                    projektname = it[PLAN_VIEW.PROJEKTENAME]!!,
                    projekt = it[PLAN_VIEW.PROJEKT]!!,
                    stunde = it[PLAN_VIEW.STUNDE]!!,
                    planbis = it[PLAN_VIEW.PLAN_BIS],
                    planvon = it[PLAN_VIEW.PLAN_VON],
                    aktualisiertAm = it[PLAN_VIEW.AKTUALISIERTAM]!!,
                    aktualisiertVon = it[PLAN_VIEW.AKTUALISIERTVON]!!,
                    erstelltAm = it[PLAN_VIEW.ERSTELLTAM]!!,
                    erstelltVon = it[PLAN_VIEW.ERSTELLTVON]!!,
                )
            }
    }
    private fun holePlanZeitView(dsl: DSLContext):List<RestPlanZeitView>{
        return dsl.select()
            .from(PLANZEIT_VIEW)
            .fetch {
                RestPlanZeitView(
                    projekt = it[PLANZEIT_VIEW.PROJEKT]!!,
                    planvon = it[PLANZEIT_VIEW.PLAN_VON]!!,
                    totalstunde = it[PLANZEIT_VIEW.TOTALSTUNDE]!!
                )
            }
    }

    fun Route.viewRouten(dsl: DSLContext){
        get("/view"){
            logger.info { "get View!" }
            val view = ViewService.holeView(dsl)
            call.respond(view)
        }

        get("/plan_zeit"){
            logger.info { "get Plan-View!" }
            val planView = ViewService.holePlanView(dsl)
            call.respond(planView)
        }
        get("/planzeit_view"){
            logger.info { "get Plan Zeit - View!" }
            val planzeit=ViewService.holePlanZeitView(dsl)
            call.respond(planzeit)
        }
    }

}