package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class MonobankStatusResponse(
    val connected: Boolean,
    val connection: ConnectionInfo? = null,
    val accountsCount: Int = 0
)