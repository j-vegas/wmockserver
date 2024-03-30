package com.wmockserver.controllers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.Options
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServerFactory
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.Response
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.RemovalListener
import com.wmockserver.dto.ClientDeviceType
import com.wmockserver.dto.core.InstanceListResponse
import com.wmockserver.dto.core.WiremockInstanceHolder
import com.wmockserver.utils.Constants.DEVICE_OS
import com.wmockserver.utils.Log
import com.wmockserver.utils.ext.isAdmin
import com.wmockserver.utils.ext.prefix
import com.wmockserver.utils.extension.*
import io.ktor.server.plugins.*
import java.util.concurrent.TimeUnit

object WiremockPoolController {

    private const val MAX_INSTANCES = 100L

    private val wiremockInstancesLogs: Cache<String, StringBuilder> = CacheBuilder.newBuilder()
        .expireAfterAccess(1, TimeUnit.DAYS)
        .maximumSize(100)
        .build()

    private val wiremockInstances: Cache<String, WiremockInstanceHolder> = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .maximumSize(100)
        .removalListener(RemovalListener<String, WiremockInstanceHolder> {
            it.value?.wiremock?.resetMappings()
        })
        .build()

    fun processRequest(instanceKey: String, request: Request): Response? {
        val wiremockInstance = wiremockInstances.get(instanceKey) { initWiremockInstance() }

        wiremockInstance.updateDeviceType(ClientDeviceType.resolve(request.getHeader(DEVICE_OS)))

        Log.logger.debug("${request.prefix()} request [$instanceKey]: '${request.absoluteUrl}' with body \"${request.bodyAsString}\"")
        val response =
            if (request.isAdmin())
                wiremockInstance.httpServer.adminRequest(request)
            else
                wiremockInstance.httpServer.stubRequest(request)
        Log.logger.debug("${request.prefix()} response [$instanceKey]: '${response.status}' '${request.absoluteUrl}' with body \"${response.bodyAsString}\"")
        return response
    }

    fun instanceLog(instanceKey: String?): String {
        if (instanceKey == null) throw BadRequestException("Request error, instanceKey should be in the path")

        return wiremockInstancesLogs.getIfPresent(instanceKey)?.toString()
            ?: throw BadRequestException("Logs not found for instance '$instanceKey'")
    }

    fun instancesList(): List<InstanceListResponse> =
        wiremockInstances.asMap().map { InstanceListResponse(it.key, it.value.deviceType) }

    private fun instancesCount(): Long = wiremockInstances.size()

    private fun initWiremockInstance(): WiremockInstanceHolder {
        if (instancesCount() >= MAX_INSTANCES) {
            throw BadRequestException("Sorry, max $MAX_INSTANCES clients amount is reached")
        }

        val httpServerFactory = DirectCallHttpServerFactory()
        val wiremock = WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .httpServerFactory(httpServerFactory)
                .usingFilesUnderClasspath("mocks")
                .enableBrowserProxying(true)
                .trustAllProxyTargets(true)
                .stubRequestLoggingDisabled(true)
                .useChunkedTransferEncoding(Options.ChunkedEncodingPolicy.BODY_FILE)
                .disableRequestJournal()
                .gzipDisabled(true)
                .extensions(
                    ResponseHeaderInfoTransformer(),
                    ResponseHeaderContentTransformer(),
                    ResponseBodyModelTransformer(),
                    ResponseBodyJsonPathTransformer(),
                    ResponseBodyFromRequestTransformer(),
                )
        )
        return WiremockInstanceHolder(
            httpServer = httpServerFactory.httpServer,
            wiremock = wiremock,
        )
    }
}
