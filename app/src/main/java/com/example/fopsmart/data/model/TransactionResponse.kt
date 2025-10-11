package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class TransactionResponse (
    @SerializedName("transactions")
    val transactions: List<Transaction>
)