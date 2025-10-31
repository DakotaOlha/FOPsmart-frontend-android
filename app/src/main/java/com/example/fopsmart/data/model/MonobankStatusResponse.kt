package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class MonobankStatusResponse(
    @SerializedName("connected")
    val connected: Boolean,

    @SerializedName("bankName")
    val bankName: String? = null,

    @SerializedName("accountNumber")
    val accountNumber: String? = null,

    @SerializedName("message")
    val message: String? = null
)