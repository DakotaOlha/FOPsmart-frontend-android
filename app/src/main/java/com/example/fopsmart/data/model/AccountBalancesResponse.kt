package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class AccountBalancesResponse(
    @SerializedName("balances")
    val balances: List<AccountBalance> = emptyList()
)
