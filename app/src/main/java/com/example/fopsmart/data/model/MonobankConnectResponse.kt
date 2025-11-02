package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class MonobankConnectResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("connection")
    val connection: ConnectionInfo? = null,

    @SerializedName("accounts")
    val accounts: List<MonobankAccount> = emptyList()
)
