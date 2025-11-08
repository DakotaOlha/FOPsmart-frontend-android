package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class DashboardData(
    @SerializedName("period")
    val period: PeriodInfo,
    @SerializedName("income")
    val income: IncomeInfo,
    @SerializedName("expenses")
    val expenses: ExpensesInfo,
    @SerializedName("netIncome")
    val netIncome: Double,
    @SerializedName("topCategories")
    val topCategories: List<CategoryInfo>,
    @SerializedName("dailyTrends")
    val dailyTrends: List<Trend>,
    @SerializedName("limitStatus")
    val limitStatus: LimitStatus
)