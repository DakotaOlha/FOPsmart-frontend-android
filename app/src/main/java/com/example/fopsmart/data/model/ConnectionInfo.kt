package com.example.fopsmart.data.model

data class ConnectionInfo(
    val clientName: String,
    val clientId: String,
    val lastSync: String? = null,
    val connectedAt: String? = null
)
