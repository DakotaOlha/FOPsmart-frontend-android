package com.example.fopsmart.data.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)