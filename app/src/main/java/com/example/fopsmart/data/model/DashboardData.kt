package com.example.fopsmart.data.model

data class DashboardData(
    val period: PeriodInfo,
    val income: IncomeInfo,
    val expenses: ExpensesInfo,
    val netIncome: Double,
    val limitStatus: LimitStatus
)