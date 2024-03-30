package com.wmockserver

import com.wmockserver.routes.healthCheck
import com.wmockserver.routes.mockSession
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

suspend fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            healthCheck()
            mockSession()
        }
        install(CallLogging) {
            level = Level.DEBUG
            format { call ->
                val httpMethod = call.request.httpMethod.value
                val headers = call.request.headers.entries().joinToString(",") { h -> "${h.key}:${h.value}" }
                val path = call.request.path()
                val query = call.request.queryString()
                "Method: $httpMethod, Path: $path, Query: $query, Headers: $headers"
            }
        }
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                disableHtmlEscaping()
            }
        }
        install(CORS) {
            anyHost()
        }
        install(StatusPages) {
            exception { call: ApplicationCall, ex: BadRequestException ->
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "")
            }
        }
    }.start(wait = true)
}
