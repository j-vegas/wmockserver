package com.wmockserver.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

private const val HEALTH_CHECK = "/healthCheck"

private val DEPLOY_TIME = LocalDateTime.now().toString()

fun Route.healthCheck() {
    get(HEALTH_CHECK) {
        call.respondText("WMOCKSERVER is Up and Running at $DEPLOY_TIME")
    }
}