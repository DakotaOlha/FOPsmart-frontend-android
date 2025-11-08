package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class MonthIncome(
    @SerializedName("month")
    val month: Int,

    @SerializedName("monthName")
    val monthName: String? = null,

    @SerializedName("income")
    val income: Double,

    @SerializedName("percentage")
    val percentage: String
)
