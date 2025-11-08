package com.example.fopsmart.data.model

data class IncomeVsExpensesResponse(
    val fopOnly: Boolean,
    val groupBy: String,
    val periods: Int,
    val data: List<ComparisonData>
)
