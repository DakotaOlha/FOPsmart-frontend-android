package com.example.fopsmart.data.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val fopGroup: Number
)