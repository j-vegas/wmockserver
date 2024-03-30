package com.wmockserver.utils.ext

import com.github.tomakehurst.wiremock.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.util.*
import java.util.*
import java.util.Collections.addAll

suspend fun ApplicationRequest.toWiremockRequest(): AdaptedWiremockRequest {
    return AdaptedWiremockRequest(this, if (this.isMultipart()) "" else call.receiveText())
}

class AdaptedWiremockRequest(private val origin: ApplicationRequest, private val body: String) : Request {
    private var url = origin.local.uri.replace("\\/\\/+".toRegex(), "/")
    private var customHeaders = mutableMapOf<String, String>()

    override fun getUrl(): String = url

    fun setUrl(url: String) {
        this.url = url
    }

    override fun getAbsoluteUrl(): String =
        "${origin.local.scheme}://${origin.local.host}:${origin.port()}${origin.local.uri}"

    override fun getMethod(): RequestMethod = RequestMethod(origin.httpMethod.value)

    override fun getScheme(): String = origin.local.scheme

    override fun getHost(): String = origin.host()

    override fun getPort(): Int = origin.port()

    override fun getClientIp(): String = origin.call.request.origin.remoteHost

    override fun getHeader(key: String?): String = customHeaders[key ?: ""] ?: origin.headers[key ?: ""] ?: ""

    override fun header(key: String?): HttpHeader =
        HttpHeader(key, customHeaders[key ?: ""] ?: origin.headers[key ?: ""])

    override fun contentTypeHeader(): ContentTypeHeader = ContentTypeHeader(origin.contentType().contentType)

    override fun getHeaders(): HttpHeaders {
        var headers = HttpHeaders()
        origin.headers.entries().forEach {
            headers = headers.plus(HttpHeader(it.key, it.value))
        }
        customHeaders.forEach {
            headers = headers.plus(HttpHeader(it.key, it.value))
        }
        return headers
    }

    override fun containsHeader(key: String?): Boolean = origin.headers.contains(key ?: "")

    override fun getAllHeaderKeys(): MutableSet<String> =
        origin.headers.names().toMutableSet().also { addAll(customHeaders.keys) }

    override fun getCookies(): MutableMap<String, Cookie> {
        return origin.cookies.rawCookies.entries.associate { it.key to Cookie(it.value) }.toMutableMap()
    }

    override fun queryParameter(key: String?): QueryParameter {
        origin.queryParameters[key ?: ""].takeIf { it != null }?.let { return QueryParameter(key, listOf(it)) }

        return QueryParameter(key, emptyList())
    }

    override fun formParameter(key: String?): FormParameter {
        origin.rawQueryParameters[key ?: ""].takeIf { it != null }?.let { return FormParameter(key, listOf(it)) }

        return FormParameter(key, emptyList())
    }

    override fun formParameters(): MutableMap<String, FormParameter> {
        return origin.rawQueryParameters.entries().associate { it.key to FormParameter(it.key, it.value) }
            .toMutableMap()
    }

    override fun getBody(): ByteArray = body.toByteArray()

    override fun getBodyAsString(): String = body

    override fun getBodyAsBase64(): String = body.encodeBase64()

    override fun isMultipart(): Boolean = origin.isMultipart()

    override fun getParts(): MutableCollection<Request.Part> {
        val parts = mutableListOf<Request.Part>()
        return if (!isMultipart) parts else mutableListOf()
    }

    override fun getPart(name: String?): Request.Part {
        TODO("Not yet implemented")
    }

    override fun isBrowserProxyRequest(): Boolean = false

    override fun getOriginalRequest(): Optional<Request> {
        TODO("Not yet implemented")
    }

    override fun getProtocol(): String = origin.httpVersion

}
