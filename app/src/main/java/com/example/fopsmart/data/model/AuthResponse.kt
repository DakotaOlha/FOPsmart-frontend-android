package com.example.fopsmart.data.model

data class AuthResponse(
    val user: UserDto,
    val token: String
)