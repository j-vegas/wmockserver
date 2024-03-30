package com.wmockserver.utils.extension

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.common.Metadata
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import java.io.IOException
import java.nio.charset.StandardCharsets

class ResponseBodyModelTransformer : ResponseDefinitionTransformer() {
    private val jsonMapper = ObjectMapper()

    override fun getName(): String = "body-model-transform"

    override fun applyGlobally(): Boolean = false

    override fun transform(
        request: Request,
        responseDefinition: ResponseDefinition,
        files: FileSource?,
        parameters: Parameters?
    ): ResponseDefinition {
        if (hasEmptyResponseBody(responseDefinition)) {
            return responseDefinition
        }

        val responseBody = getResponseBody(responseDefinition, files)
        return ResponseDefinitionBuilder
            .like(responseDefinition).but()
            .withBodyFile(null)
            .withBody(transformResponse(responseBody, parameters))
            .build()
    }

    private fun transformResponse(response: String?, parameters: Parameters?): String? {
        if (parameters == null) return response
        var modifiedResponse = response
        try {
            val responseMap = jsonMapper.readValue(response, MutableMap::class.java).toMutableMap()
            responseMap.applyParameters(parameters)
            modifiedResponse = jsonMapper.writeValueAsString(responseMap)
        } catch (_: IOException) {

        }
        return modifiedResponse
    }

    private fun MutableMap<Any?, Any?>.applyParameters(parameters: Metadata) {
        parameters.entries.forEach { p ->
            if (p.value is Map<*, *>) {
                if (this[p.key] !is Map<*, *>) {
                    this[p.key] = p.value
                } else {
                    this[p.key] = (this[p.key] as Map<*, *>).toMutableMap().also {
                        it.applyParameters(Parameters.from(p.value))
                    }
                }
            } else {
                this[p.key] = p.value
            }
        }
    }

    private fun hasEmptyResponseBody(responseDefinition: ResponseDefinition): Boolean {
        return responseDefinition.body == null && responseDefinition.bodyFileName == null
    }

    private fun getResponseBody(responseDefinition: ResponseDefinition, fileSource: FileSource?): String? {
        return if (responseDefinition.body != null) {
            responseDefinition.body
        } else if (fileSource != null) {
            val binaryFile = fileSource.getBinaryFileNamed(responseDefinition.bodyFileName)
            String(binaryFile.readContents(), StandardCharsets.UTF_8)
        } else null
    }
}

