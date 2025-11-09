package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class UpdateFopRequest(
    @SerializedName("fopGroup") val fopGroup: Int,
    @SerializedName("taxSystem") val taxSystem: String
)
