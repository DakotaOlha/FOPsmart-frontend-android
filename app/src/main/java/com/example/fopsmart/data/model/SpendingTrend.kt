package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class SpendingTrend(
    @SerializedName("date")
    val date: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("parentCategory")
    val parentCategory: String? = null,

    @SerializedName("totalSpent")
    val totalSpent: Double
)