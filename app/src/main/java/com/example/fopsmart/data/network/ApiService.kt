package com.example.fopsmart.data.network

import com.example.fopsmart.data.model.AuthResponse
import com.example.fopsmart.data.model.LoginRequest
import com.example.fopsmart.data.model.LoginResponse
import com.example.fopsmart.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}
