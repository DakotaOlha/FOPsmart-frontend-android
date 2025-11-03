package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class TotalBalanceInfo(
    @SerializedName("currencyCode")
    val currencyCode: Int,

    @SerializedName("balance")
    val balance: Long,

    @SerializedName("accountsCount")
    val accountsCount: Int
)
