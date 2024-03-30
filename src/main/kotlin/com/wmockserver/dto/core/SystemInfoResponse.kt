package com.wmockserver.dto.core

data class SystemInfoResponse(
    val usedMemory: Long,
    val totalMemory: Long,
    val maxMemory: Long,
    val freeMemory: Long,
)
