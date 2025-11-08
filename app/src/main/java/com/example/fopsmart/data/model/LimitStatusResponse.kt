package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class LimitStatusResponse(
    @SerializedName("configured")
    val configured: Boolean,

    @SerializedName("year")
    val year: Int,

    @SerializedName("fopGroup")
    val fopGroup: Int,

    @SerializedName("currentIncome")
    val currentIncome: String,

    @SerializedName("limit")
    val limit: String,

    @SerializedName("percentage")
    val percentage: String,

    @SerializedName("remaining")
    val remaining: String,

    @SerializedName("status")
    val status: String
) {
    fun getCurrentIncomeDouble(): Double = currentIncome.toDoubleOrNull() ?: 0.0
    fun getLimitDouble(): Double = limit.toDoubleOrNull() ?: 0.0
    fun getPercentageDouble(): Double = percentage.toDoubleOrNull() ?: 0.0
    fun getRemainingDouble(): Double = remaining.toDoubleOrNull() ?: 0.0
}