package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class TransactionManualResponse(
    val message: String,
    val transaction: TransactionManual
)

data class TransactionManual(
    @SerializedName("id")
    val id: Int,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("mcc")
    val mcc: Int,
)
