package com.example.fopsmart.data.model

data class ChatResponse(
    val answer: String,
    val confidence: Number,
    val query_type: String,
    val reason: String
)