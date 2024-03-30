package com.wmockserver.utils.extension

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray
import java.nio.charset.StandardCharsets

class ResponseBodyJsonPathTransformer : ResponseDefinitionTransformer() {

    override fun getName(): String = "body-jsonpath-transform"

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

        return JsonPath.parse(response).apply {
            parameters.entries.forEach { parameter ->
                if (parameter.value != null) {
                    set(parameter.key, parameter.value)
                } else {
                    when (val target = read<Any>(parameter.key)) {
                        is JSONArray -> {
                            if (target.isNotEmpty() && (target.first() is Map<*, *> || target.first() is JSONArray))
                                delete(parameter.key)
                            else
                                set(parameter.key, parameter.value)
                        }

                        is Map<*, *> -> delete(parameter.key)
                        else -> set(parameter.key, parameter.value)
                    }
                }
            }
        }.jsonString()
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

