package com.example.fopsmart.data.network

import com.example.fopsmart.data.model.AccountBalancesResponse
import com.example.fopsmart.data.model.AuthResponse
import com.example.fopsmart.data.model.ConnectRequest
import com.example.fopsmart.data.model.DashboardData
import com.example.fopsmart.data.model.IncomeVsExpensesResponse
import com.example.fopsmart.data.model.LimitStatusResponse
import com.example.fopsmart.data.model.LimitUtilization
import com.example.fopsmart.data.model.LoginRequest
import com.example.fopsmart.data.model.LoginResponse
import com.example.fopsmart.data.model.MonobankConnectResponse
import com.example.fopsmart.data.model.MonobankStatusResponse
import com.example.fopsmart.data.model.RegisterRequest
import com.example.fopsmart.data.model.ReportGenerateRequest
import com.example.fopsmart.data.model.ReportPreview
import com.example.fopsmart.data.model.ReportTypesResponse
import com.example.fopsmart.data.model.SpendingTrendsResponse
import com.example.fopsmart.data.model.TransactionResponse
import okhttp3.ResponseBody
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


    @GET("analytics/dashboard")
    suspend fun getAnalyticsDashboard(
        @Header("Authorization") token: String,
        @Query("days") days: Int = 30
    ): Response<DashboardData>

    @GET("analytics/spending-trends")
    suspend fun getSpendingTrends(
        @Header("Authorization") token: String,
        @Query("period") period: String = "month"
    ): Response<SpendingTrendsResponse>

    @GET("analytics/income-vs-expenses")
    suspend fun getIncomeVsExpenses(
        @Header("Authorization") token: String,
        @Query("groupBy") groupBy: String = "month",
        @Query("limit") limit: Int = 12
    ): Response<IncomeVsExpensesResponse>

    @GET("analytics/limit-utilization")
    suspend fun getLimitUtilization(
        @Header("Authorization") token: String,
        @Query("year") year: Int? = null
    ): Response<LimitUtilization>

    @GET("profile/limit-status")
    suspend fun getLimitStatus(
        @Header("Authorization") token: String,
        @Query("year") year: Int? = null
    ): Response<LimitStatusResponse>

    @GET("reports/types")
    suspend fun getReportTypes(
        @Header("Authorization") token: String
    ): Response<ReportTypesResponse>

    @GET("reports/preview")
    suspend fun getReportPreview(
        @Header("Authorization") token: String,
        @Query("dateFrom") dateFrom: String?,
        @Query("dateTo") dateTo: String?
    ): Response<ReportPreview>

    @POST("reports/generate")
    suspend fun generateReport(
        @Header("Authorization") token: String,
        @Body request: ReportGenerateRequest
    ): Response<ResponseBody>
}
