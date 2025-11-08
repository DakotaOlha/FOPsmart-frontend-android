package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class LimitUtilization(
    @SerializedName("year")
    val year: Int,

    @SerializedName("configured")
    val configured: Boolean,

    @SerializedName("limit")
    val limit: Double,

    @SerializedName("totalIncome")
    val totalIncome: Double,

    @SerializedName("utilizationPercentage")
    val utilizationPercentage: Double? = null,

    @SerializedName("percentage")
    val percentage: Double? = null,

    @SerializedName("remaining")
    val remaining: Double,

    @SerializedName("monthsWithIncome")
    val monthsWithIncome: Int,

    @SerializedName("monthlyBreakdown")
    val monthlyBreakdown: List<MonthIncome>
) {
    fun getPercentage(): Double {
        return utilizationPercentage ?: percentage ?: 0.0
    }
}