package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class ExpensesInfo(
    @SerializedName("totalTransactions")
    val totalTransactions: Int,

    @SerializedName("totalAmount")
    val totalAmount: Double
)