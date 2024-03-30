package com.wmockserver.utils.extension.listeners

import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.Response
import com.wmockserver.utils.Constants.ADMIN
import com.wmockserver.utils.Constants.MOCK
import com.wmockserver.utils.ext.isAdmin
import java.text.SimpleDateFormat
import java.util.*

class LogRequestListener(private val logInstance: StringBuilder? = null) {
    private val timeStampFormatter = SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS")

    fun logRequest(request: Request) {
        logInstance?.apply {
            appendLine("${timeStamp()} <=== ${requestType(request)} request: ${request.method.value()} ${request.absoluteUrl}")
            if (!request.bodyAsString.isNullOrEmpty()) {
                appendLine("${timeStamp()} <=== body:")
                append(request.bodyAsString)
            }
        }
    }

    fun logResponse(response: Response) {
        logInstance?.apply {
            appendLine("${timeStamp()} ===> response status: ${response.status}")
            response.headers.all().forEach {
                appendLine("${timeStamp()} ===> header '${it.key()}': ${it.firstValue()}")
            }
            if (!response.bodyAsString.isNullOrEmpty()) {
                appendLine("${timeStamp()} ===> body:")
                append(response.bodyAsString)
                appendLine("${timeStamp()} ===> response end")
            }
            appendLine()
        }
    }

    private fun timeStamp(): String {
        return "[${timeStampFormatter.format(Date())}]:"
    }

    private fun requestType(request: Request): String {
        return if (request.isAdmin()) ADMIN else MOCK
    }
}