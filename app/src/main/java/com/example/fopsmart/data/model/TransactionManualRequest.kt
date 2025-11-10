package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class TransactionManualRequest(
    @SerializedName("amount")
    val amount: Long,
    @SerializedName("description")
    val description: String,
    @SerializedName("transactionDate")
    val transactionDate: String,
    @SerializedName("type")
    val type: Long,
    @SerializedName("mcc")
    val mcc: Int,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("accountId")
    val accountId: Int
)