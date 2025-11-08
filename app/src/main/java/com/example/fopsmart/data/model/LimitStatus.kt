package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class LimitStatus(
    @SerializedName("fopGroup")
    val fopGroup: Int,
    @SerializedName("currentIncome")
    val currentIncome: String,
    @SerializedName("limit")
    val limit: Double,
    @SerializedName("percentage")
    val percentage: Double,
    @SerializedName("remaining")
    val remaining: Double,
    @SerializedName("status")
    val status: String
)
