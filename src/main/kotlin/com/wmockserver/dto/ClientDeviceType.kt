package com.wmockserver.dto

enum class ClientDeviceType {
    ANDROID,
    IOS,
    BROWSER,
    UNSPECIFIED;

    companion object {
        fun resolve(deviceOs: String?): ClientDeviceType {
            return when (deviceOs?.lowercase()) {
                "android" -> ANDROID
                "ios" -> IOS
                "browser" -> BROWSER
                else -> UNSPECIFIED
            }
        }
    }
}