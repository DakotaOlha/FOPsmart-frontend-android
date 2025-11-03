package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class MonobankAccount(
    @SerializedName("id")
    val id: Int,

    @SerializedName("balance")
    val balance: String,

    @SerializedName("currencyCode")
    val currencyCode: Int,

    @SerializedName("iban")
    val iban: String,

    @SerializedName("type")
    val type: String
)
