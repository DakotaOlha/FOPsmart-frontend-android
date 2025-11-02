package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class AccountBalancesResponse(
    @SerializedName("accounts")
    val balances: List<AccountBalance> = emptyList(),

    @SerializedName("total")
    val total: List<TotalBalanceInfo> = emptyList()
)
