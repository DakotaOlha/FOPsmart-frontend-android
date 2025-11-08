package com.example.fopsmart.data

import android.util.Log
import com.example.fopsmart.data.model.ReportGenerateRequest
import com.example.fopsmart.data.model.ReportPreview
import com.example.fopsmart.data.model.ReportPreviewRequest
import com.example.fopsmart.data.model.ReportTypesResponse
import com.example.fopsmart.data.network.RetrofitClient
import okhttp3.ResponseBody

class ReportRepository {
    private val apiService = RetrofitClient.mainApi
    private val TAG = "ReportsRepository"

    suspend fun getReportTypes(token: String): Result<ReportTypesResponse> {
        return try {
            Log.d(TAG, "Завантаження типів звітів")
            val response = apiService.getReportTypes("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(TAG, "Типи звітів завантажено: ${body.types.size}")
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Порожнє тіло відповіді"))
                }
            } else {
                Log.e(TAG, "Помилка: ${response.code()}")
                Result.Error(Exception("Помилка: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в getReportTypes", e)
            Result.Error(e)
        }
    }

    suspend fun getReportPreview(
        token: String,
        request: ReportPreviewRequest
    ): Result<ReportPreview> {
        return try {
            Log.d(TAG, "Завантаження попереднього перегляду звіту: pdf")
            val response = apiService.getReportPreview("Bearer $token", request.startDate, request.endDate)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(TAG, "Попередній перегляд завантажено")
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Порожнє тіло відповіді"))
                }
            } else {
                Log.e(TAG, "Помилка: ${response.code()}")
                Result.Error(Exception("Помилка: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в getReportPreview", e)
            Result.Error(e)
        }
    }

    suspend fun generateReport(
        token: String,
        request: ReportGenerateRequest
    ): Result<ResponseBody> {
        return try {
            Log.d(TAG, "Генерація звіту: ${request.type}, формат: pdf")
            val response = apiService.generateReport("Bearer $token", request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d(TAG, "Звіт згенеровано успішно")
                    Result.Success(body)
                } else {
                    Result.Error(Exception("Порожнє тіло відповіді"))
                }
            } else {
                Log.e(TAG, "Помилка: ${response.code()}")
                Result.Error(Exception("Помилка генерації звіту: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в generateReport", e)
            Result.Error(e)
        }
    }
}