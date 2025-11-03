package com.example.fopsmart.data.network

import com.example.fopsmart.data.model.AccountBalancesResponse
import com.example.fopsmart.data.model.AuthResponse
import com.example.fopsmart.data.model.ConnectRequest
import com.example.fopsmart.data.model.LoginRequest
import com.example.fopsmart.data.model.LoginResponse
import com.example.fopsmart.data.model.MonobankConnectResponse
import com.example.fopsmart.data.model.MonobankStatusResponse
import com.example.fopsmart.data.model.RegisterRequest
import com.example.fopsmart.data.model.TransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface MainApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null,
        @Query("accountId") accountId: Int? = null,
        @Query("limit") limit: Int = 50,
        @Query("type") type: String = "all"
    ): Response<TransactionResponse>

    @GET("transactions/balances")
    suspend fun getAccountBalances(
        @Header("Authorization") token: String
    ): Response<AccountBalancesResponse>

    @GET("monobank/status")
    suspend fun getMonoStatus(
        @Header("Authorization") token: String
    ): Response<MonobankStatusResponse>

    @POST("monobank/connect")
    suspend fun setMonoConnection(
        @Header("Authorization") token: String,
        @Body request: ConnectRequest
    ): Response<MonobankConnectResponse>

}
