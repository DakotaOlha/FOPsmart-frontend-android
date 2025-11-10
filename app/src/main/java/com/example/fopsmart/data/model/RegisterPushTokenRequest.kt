package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class RegisterPushTokenRequest(
    @SerializedName("fcmToken")
    val fcmToken: String,

    @SerializedName("platform")
    val platform: String = "android",

    @SerializedName("deviceInfo")
    val deviceInfo: DeviceInfo? = null
)

data class DeviceInfo(
    @SerializedName("model")
    val model: String,

    @SerializedName("os")
    val os: String,

    @SerializedName("appVersion")
    val appVersion: String
)

data class RegisterPushTokenResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("token")
    val token: TokenInfo
)

data class TokenInfo(
    @SerializedName("id")
    val id: Int,

    @SerializedName("platform")
    val platform: String,

    @SerializedName("registeredAt")
    val registeredAt: String
)