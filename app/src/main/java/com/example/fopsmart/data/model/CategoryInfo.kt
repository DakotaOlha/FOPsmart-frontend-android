package com.example.fopsmart.data.model

data class CategoryInfo (
    val mcc: Long,
    val category: String,
    val transactionCount: Int,
    val totalSpent: Double
)