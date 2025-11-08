package com.example.fopsmart.data.model

data class SpendingTrendsResponse(
    val fopOnly: Boolean,
    val period: String,
    val trends: List<SpendingTrend>
)
