package com.wmockserver.utils.extension

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import com.jayway.jsonpath.JsonPath
import java.nio.charset.StandardCharsets

class ResponseBodyFromRequestTransformer : ResponseDefinitionTransformer() {

    override fun getName(): String = "body-from-request-transform"

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
            .withBody(transformResponse(responseBody, request, parameters))
            .build()
    }

    private fun transformResponse(response: String?, request: Request, parameters: Parameters?): String? {
        if (parameters == null) return response

        val responseJson = JsonPath.parse(response)
        val requestJson = JsonPath.parse(request.bodyAsString)
        val newValues: Map<String, Any> = requestJson.read(parameters["requestPath"] as String)
        newValues.forEach {
            responseJson.put(parameters["responsePath"] as String, it.key, it.value)
        }
        return responseJson.jsonString()
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

