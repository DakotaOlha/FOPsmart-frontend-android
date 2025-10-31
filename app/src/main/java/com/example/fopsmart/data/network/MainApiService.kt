package com.example.fopsmart.data.network

import com.example.fopsmart.data.model.AuthResponse
import com.example.fopsmart.data.model.LoginRequest
import com.example.fopsmart.data.model.LoginResponse
import com.example.fopsmart.data.model.MonobankStatusResponse
import com.example.fopsmart.data.model.RegisterRequest
import com.example.fopsmart.data.model.TransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MainApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String
    ): Response<TransactionResponse>

    @GET("monobank/status")
    suspend fun getMonoStatus(
        @Header("Authorization") token: String
    ): Response<MonobankStatusResponse>

}
