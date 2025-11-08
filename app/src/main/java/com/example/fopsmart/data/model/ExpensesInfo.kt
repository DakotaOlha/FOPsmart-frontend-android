package com.example.fopsmart.data.model

data class ExpensesInfo(
    val total: Double,
    val count: Int,
    val average: Double,
    val trend: String,
    val topCategories: List<CategoryAmount>
)