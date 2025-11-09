package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class FopLimitInfo(
    @SerializedName("annual") val annual: Int,
    @SerializedName("description") val description: String
)
