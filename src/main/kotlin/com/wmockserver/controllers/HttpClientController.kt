package com.wmockserver.controllers

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

class HttpClientController {

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
    }
}

fun initHttpClient() = HttpClientController().httpClient