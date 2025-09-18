package com.example.fopsmart.data.model

data class LoginResponse(
    val user: UserDto,
    val token: String
)

data class UserDto(
    val id: String,
    val email: String
)