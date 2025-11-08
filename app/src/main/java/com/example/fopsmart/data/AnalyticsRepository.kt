package com.example.fopsmart.data

import android.util.Log
import com.example.fopsmart.data.model.ComparisonData
import com.example.fopsmart.data.model.DashboardData
import com.example.fopsmart.data.model.IncomeVsExpensesResponse
import com.example.fopsmart.data.model.LimitUtilization
import com.example.fopsmart.data.model.SpendingTrend
import com.example.fopsmart.data.model.SpendingTrendsResponse
import com.example.fopsmart.data.network.RetrofitClient
import com.example.fopsmart.ui.stats.*

class AnalyticsRepository {

    private val apiService = RetrofitClient.mainApi
    private val TAG = "AnalyticsRepository"

    suspend fun getDashboard(token: String, days: Int = 30): Result<DashboardData> {
        return try {
            Log.d(TAG, "Завантаження дашборду")
            val response = apiService.getAnalyticsDashboard(
                token = "Bearer $token",
                days = days
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(TAG, "Дашборд завантажено успішно")
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Порожнє тіло відповіді"))
                }
            } else {
                Log.e(TAG, "Помилка завантаження дашборду: ${response.code()}")
                Result.Error(Exception("Помилка: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в getDashboard", e)
            Result.Error(e)
        }
    }

    suspend fun getSpendingTrends(token: String, period: String): Result<SpendingTrendsResponse> {
        return try {
            Log.d(TAG, "Завантаження трендів витрат")
            val response = apiService.getSpendingTrends(
                token = "Bearer $token",
                period = period
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(TAG, "Тренди завантажено: ${body.trends.size} категорій")
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Порожнє тіло відповіді"))
                }
            } else {
                Result.Error(Exception("Помилка: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в getSpendingTrends", e)
            Result.Error(e)
        }
    }

    suspend fun getIncomeVsExpenses(
        token: String,
        groupBy: String,
        limit: Int
    ): Result<IncomeVsExpensesResponse> {
        return try {
            Log.d(TAG, "Завантаження порівняння доходів та витрат")
            val response = apiService.getIncomeVsExpenses(
                token = "Bearer $token",
                groupBy = groupBy,
                limit = limit
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(TAG, "Порівняння завантажено успішно")
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Порожнє тіло відповіді"))
                }
            } else {
                Log.e(TAG, "Помилка завантаження порівняння: ${response.code()}")
                Result.Error(Exception("Помилка: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в getIncomeVsExpenses", e)
            Result.Error(e)
        }
    }

    suspend fun getLimitUtilization(token: String, year: Int? = null): Result<LimitUtilization> {
        return try {
            Log.d(TAG, "Завантаження використання ліміту")
            val response = apiService.getLimitUtilization(
                token = "Bearer $token",
                year = year
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(TAG, "Ліміт завантажено успішно")
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Порожнє тіло відповіді"))
                }
            } else {
                Log.e(TAG, "Помилка завантаження ліміту: ${response.code()}")
                Result.Error(Exception("Помилка: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в getLimitUtilization", e)
            Result.Error(e)
        }
    }
}