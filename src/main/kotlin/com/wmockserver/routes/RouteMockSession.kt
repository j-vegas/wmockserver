package com.wmockserver.routes

import com.github.tomakehurst.wiremock.http.Response
import com.wmockserver.controllers.WiremockPoolController
import com.wmockserver.utils.Constants.DEVICE_ID
import com.wmockserver.utils.ext.toWiremockRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Route.mockSession() {
    get("{...}", request)
    get("{...}/", request)

    post("{...}", request)
    post("{...}/", request)

    put("{...}", request)
    put("{...}/", request)

    patch("{...}", request)
    patch("{...}/", request)

    delete("{...}", request)
    delete("{...}/", request)

    head("{...}", request)
    head("{...}/", request)
}

val request: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit = {
    val deviceId = call.verifyAndGetDeviceId()

    val wiremockResponse = WiremockPoolController.processRequest(deviceId, call.request.toWiremockRequest())

    call.response.adaptWiremockResponse(wiremockResponse)
    val contentType = wiremockResponse?.headers?.getHeader("Content-Type")?.takeIf { it.isPresent }?.firstValue()
        ?: "application/json;charset=UTF-8"
    call.respondText(
        wiremockResponse?.bodyAsString ?: "{}",
        ContentType.parse(contentType)
    )
}

private fun ApplicationCall.verifyAndGetDeviceId(): String {
    if (!request.headers.contains(DEVICE_ID)) {
        throw BadRequestException("Missing '$DEVICE_ID' header in request.")
    }

    return request.headers[DEVICE_ID].toString()
}

private fun ApplicationResponse.adaptWiremockResponse(response: Response?) {
    response?.let {
        setStatus(response)
        setHeaders(response)
    } ?: run {
        nullWiremockResponse()
    }
}

private fun ApplicationResponse.setStatus(response: Response) {
    status(HttpStatusCode.fromValue(response.status))
}

private fun ApplicationResponse.setHeaders(response: Response) {
    response.headers.all().forEach {
        if (!HttpHeaders.isUnsafe(it.key())) {
            try {
                header(it.key(), it.firstValue())
            } catch (_: Exception) {
            }
        }
    }
}

private fun ApplicationResponse.nullWiremockResponse() {
    status(HttpStatusCode.fromValue(404))
}
