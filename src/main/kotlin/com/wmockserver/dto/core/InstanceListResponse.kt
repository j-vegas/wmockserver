package com.wmockserver.dto.core

import com.wmockserver.dto.ClientDeviceType

data class InstanceListResponse(
    val id: String,
    val deviceType: ClientDeviceType
)
