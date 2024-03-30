package com.wmockserver.dto.core

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServer
import com.wmockserver.dto.ClientDeviceType

data class WiremockInstanceHolder(
    val httpServer: DirectCallHttpServer,
    val wiremock: WireMockServer,
    var deviceType: ClientDeviceType = ClientDeviceType.UNSPECIFIED
) {
    fun updateDeviceType(deviceType: ClientDeviceType) {
        if (this.deviceType == ClientDeviceType.UNSPECIFIED && this.deviceType != deviceType) {
            this.deviceType = deviceType
        }
    }
}
