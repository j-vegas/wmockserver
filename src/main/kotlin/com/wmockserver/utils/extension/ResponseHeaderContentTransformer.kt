package com.wmockserver.utils.extension

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.ResponseDefinition

class ResponseHeaderContentTransformer : ResponseDefinitionTransformer() {

    override fun getName(): String = "response-header-content"

    override fun applyGlobally(): Boolean = true

    override fun transform(
        request: Request,
        responseDefinition: ResponseDefinition,
        files: FileSource?,
        parameters: Parameters?
    ): ResponseDefinition {
        val type = responseDefinition.bodyFileName?.let {
            if (it.endsWith(".json")) "application/json;charset=UTF-8"
            else "application/octet-stream"
        } ?: "application/json;charset=UTF-8"
        return ResponseDefinitionBuilder
            .like(responseDefinition).but()
            .withHeader("Content-Type", type)
            .build()
    }
}

