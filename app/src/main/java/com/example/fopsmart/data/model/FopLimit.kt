package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class FopLimit(
    @SerializedName("annual") val annual: Int,
    @SerializedName("description") val description: String,
    @SerializedName("taxRate") val taxRate: Int?
)
