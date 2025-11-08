package com.example.fopsmart.data.model

data class SpendingTrend(
    val category: String,
    val amount: Double,
    val transactionCount: Int,
    val averageAmount: Double
)