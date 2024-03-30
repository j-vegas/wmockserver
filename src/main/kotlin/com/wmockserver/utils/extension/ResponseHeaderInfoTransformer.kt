package com.wmockserver.utils.extension

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import com.wmockserver.utils.Constants.DEVICE_ID
import com.wmockserver.utils.Constants.DEVICE_OS

class ResponseHeaderInfoTransformer : ResponseDefinitionTransformer() {

    override fun getName(): String = "response-header-info"

    override fun applyGlobally(): Boolean = true

    override fun transform(
        request: Request,
        responseDefinition: ResponseDefinition,
        files: FileSource?,
        parameters: Parameters?
    ): ResponseDefinition {
        return ResponseDefinitionBuilder
            .like(responseDefinition).but()
            .withHeader(DEVICE_ID, request.getHeader(DEVICE_ID))
            .withHeader(DEVICE_OS, request.getHeader(DEVICE_OS))
            .withHeader("Matched-Stub-File", responseDefinition.bodyFileName ?: "")
            .withHeader("Matched-Stub-Modified", (!responseDefinition.transformerParameters.isNullOrEmpty()).toString())
            .build()
    }
}

