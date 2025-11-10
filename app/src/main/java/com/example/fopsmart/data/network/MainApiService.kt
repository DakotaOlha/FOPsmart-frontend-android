package com.example.fopsmart.data.network

import com.example.fopsmart.data.model.AccountBalancesResponse
import com.example.fopsmart.data.model.AddTransactionRequest
import com.example.fopsmart.data.model.AddTransactionResponse
import com.example.fopsmart.data.model.AuthResponse
import com.example.fopsmart.data.model.ChangePasswordRequest
import com.example.fopsmart.data.model.ChangePasswordResponse
import com.example.fopsmart.data.model.ConnectRequest
import com.example.fopsmart.data.model.DashboardData
import com.example.fopsmart.data.model.DeleteAccountRequest
import com.example.fopsmart.data.model.DeleteAccountResponse
import com.example.fopsmart.data.model.FopConfigResponse
import com.example.fopsmart.data.model.IncomeVsExpensesResponse
import com.example.fopsmart.data.model.LimitStatusResponse
import com.example.fopsmart.data.model.LimitUtilization
import com.example.fopsmart.data.model.LoginRequest
import com.example.fopsmart.data.model.LoginResponse
import com.example.fopsmart.data.model.MonobankConnectResponse
import com.example.fopsmart.data.model.MonobankStatusResponse
import com.example.fopsmart.data.model.ProfileResponse
import com.example.fopsmart.data.model.RegisterPushTokenRequest
import com.example.fopsmart.data.model.RegisterPushTokenResponse
import com.example.fopsmart.data.model.RegisterRequest
import com.example.fopsmart.data.model.ReportGenerateRequest
import com.example.fopsmart.data.model.ReportPreview
import com.example.fopsmart.data.model.ReportTypesResponse
import com.example.fopsmart.data.model.SpendingTrendsResponse
import com.example.fopsmart.data.model.TransactionManualRequest
import com.example.fopsmart.data.model.TransactionManualResponse
import com.example.fopsmart.data.model.TransactionResponse
import com.example.fopsmart.data.model.UpdateFopRequest
import com.example.fopsmart.data.model.UpdateFopResponse
import com.example.fopsmart.data.model.UpdateProfileRequest
import com.example.fopsmart.data.model.UpdateProfileResponse
import com.example.fopsmart.data.model.UpdateTransactionManual
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @POST("transactions/manual")
    suspend fun addTransaction(
        @Header("Authorization") token: String,
        @Body request: AddTransactionRequest
    ): Response<AddTransactionResponse>

    @PUT("transaction/manual")
    suspend fun updateTransaction(
        @Header("Authorization") token: String,
        @Query("id") id: Int,
        @Body request: UpdateTransactionManual
    )

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

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    @PUT("profile/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ChangePasswordResponse>

    @GET("profile/fop")
    suspend fun getFopConfig(
        @Header("Authorization") token: String
    ): Response<FopConfigResponse>

    @PUT("profile/fop")
    suspend fun updateFopConfig(
        @Header("Authorization") token: String,
        @Body request: UpdateFopRequest
    ): Response<UpdateFopResponse>

    @HTTP(method = "DELETE", path = "profile/delete", hasBody = true)
    suspend fun deleteAccount(
        @Header("Authorization") token: String,
        @Body request: DeleteAccountRequest
    ): Response<DeleteAccountResponse>

    @POST("notifications/push/register")
    suspend fun registerPushToken(
        @Header("Authorization") token: String,
        @Body request: RegisterPushTokenRequest
    ): Response<RegisterPushTokenResponse>

    @POST("notifications/push/deactivate")
    suspend fun deactivatePushToken(
        @Header("Authorization") token: String,
        @Body request: DeactivatePushTokenRequest
    ): Response<DeactivatePushTokenResponse>

    data class DeactivatePushTokenRequest(
        @SerializedName("platform")
        val platform: String = "android"
    )

    data class DeactivatePushTokenResponse(
        @SerializedName("message")
        val message: String
    )

}
