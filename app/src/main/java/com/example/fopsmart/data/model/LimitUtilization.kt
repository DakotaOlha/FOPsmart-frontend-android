package com.example.fopsmart.data.model

data class LimitUtilization(
    val year: Int,
    val configured: Boolean,
    val limit: Double,
    val totalIncome: Double,
    val utilizationPercentage: Double,
    val remaining: Double,
    val monthsWithIncome: Int,
    val monthlyBreakdown: List<MonthIncome>
)
