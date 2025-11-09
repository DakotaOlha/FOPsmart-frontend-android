package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class UpdateFopResponse(
    @SerializedName("message") val message: String,
    @SerializedName("fopGroup") val fopGroup: Int,
    @SerializedName("taxSystem") val taxSystem: String,
    @SerializedName("limit") val limit: FopLimitInfo
)
