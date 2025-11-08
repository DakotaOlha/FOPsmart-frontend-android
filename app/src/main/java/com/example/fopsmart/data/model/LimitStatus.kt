package com.example.fopsmart.data.model

data class LimitStatus(
    val fopGroup: Int,
    val currentIncome: String,
    val limit: Double,
    val percentage: Double,
    val remaining: Double,
    val status: String
)
