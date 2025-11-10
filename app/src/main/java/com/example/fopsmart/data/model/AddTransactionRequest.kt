package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class AddTransactionRequest(
    @SerializedName("amount")
    val amount: Double,

    @SerializedName("description")
    val description: String,

    @SerializedName("transactionDate")
    val transactionDate: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("mcc")
    val mcc: String? = null,

    @SerializedName("comment")
    val comment: String? = null,

    @SerializedName("accountId")
    val accountId: Int
)