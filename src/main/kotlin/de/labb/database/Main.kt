package de.labb.database

import de.labb.database.business.BenutzerService.benutzerRouten
import de.labb.database.business.PlanService.planRouten
import de.labb.database.business.ProjektService.projektRouten
import de.labb.database.business.ViewService.viewRouten
import de.labb.database.business.ZeitenService.zeitRouten
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule


import mu.KotlinLogging
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateSerializer : KSerializer<LocalDate>{
    private val logger= KotlinLogging.logger{}
    override val descriptor :SerialDescriptor= PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value:LocalDate){
        val result =value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        encoder.encodeString(result)
    }
    override fun deserialize(decoder: Decoder): LocalDate {
        try {
            return LocalDate.parse(decoder.decodeString())

        } catch (e: Exception){
            logger.error ("",e)
            throw e
        }
    }
}

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private  val logger =KotlinLogging.logger{}
    override  val descriptor: SerialDescriptor= PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value:LocalDateTime){
        val result = value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        encoder.encodeString(result)
    }
    override fun deserialize(decoder: Decoder) :LocalDateTime {
        try {
            val formatt=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val result =LocalDateTime.parse(decoder.decodeString(),formatt)
            return result
        } catch (e: Exception){
            logger.error("",e)
            throw e
        }
    }
}

fun main() {
    val userName: String = "hozdemir"
    val password: String = "sql000"
    val port = "7120"
    val datenbank = "fullprojekt"
    val url: String = "jdbc:postgresql://localhost:$port/$datenbank"

    val con: Connection = DriverManager.getConnection(url, userName, password)
    con.schema = "ubung"

    val dsl: DSLContext = DSL.using(con, SQLDialect.POSTGRES)

    val logger = KotlinLogging.logger {}


    val server = embeddedServer(Netty, port = 8090) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true

                serializersModule = SerializersModule {
                    contextual(LocalDate::class, LocalDateSerializer)
                    contextual(LocalDateTime::class, LocalDateTimeSerializer)
                }
            })
        }
        routing {
            route("/api"){
                get("/hello"){
                    call.respondText("Hello World")
                }
                benutzerRouten(dsl)

                projektRouten(dsl)

                zeitRouten(dsl)

                planRouten(dsl)

                viewRouten(dsl)
            }
        }
    }
    logger.info { "Server wurde gestartet!" }
    server.start(wait = true)
}


