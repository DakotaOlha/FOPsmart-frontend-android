package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class UpdateTransactionManual(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("transactionDate")
    val transactionDate: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("mcc")
    val mcc: Int,
    @SerializedName("comment")
    val comment: String
)