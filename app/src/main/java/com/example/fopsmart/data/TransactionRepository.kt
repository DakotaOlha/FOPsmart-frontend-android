package com.example.fopsmart.data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.fopsmart.data.model.ConnectRequest
import com.example.fopsmart.data.model.Transaction
import com.example.fopsmart.data.model.MonobankStatusResponse
import com.example.fopsmart.data.network.RetrofitClient
import com.example.fopsmart.data.network.RetrofitClient.mainApi
import java.io.IOException

class TransactionRepository {
    private val api = RetrofitClient.mainApi

    suspend fun getTransactions(token: String): Result<List<Transaction>> {
        return try {
            val response = api.getTransactions("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body.transactions)
                } else {
                    Result.Error(IOException("Empty response body"))
                }
            } else {
                Result.Error(IOException("Error loading transactions: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }

    suspend fun checkBankStatus(token: String): Result<Boolean> {
        return try {
            val response = api.getMonoStatus("Bearer $token")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body.connected)
                } else {
                    Result.Error(IOException("Empty response body"))
                }
            } else {
                Result.Error(IOException("Error checking bank status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }


    suspend fun connectMonobank(token: String, monoToken: String): Result<Boolean> {
        return try {
            Log.d(TAG, "Відправляємо запит на підключення Монобанку")
            Log.d(TAG, "User Token: ${token.take(20)}...")
            Log.d(TAG, "Mono Token: ${monoToken.take(20)}...")

            val request = ConnectRequest(token = monoToken)
            Log.d(TAG, "Request body: $request")

            val response = mainApi.setMonoConnection("Bearer $token", request)

            Log.d(TAG, "Response code: ${response.code()}")
            Log.d(TAG, "Response headers: ${response.headers()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Response body: $body")
                val isConnected = body?.success ?: false
                Result.Success(isConnected)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Помилка connectMonobank: ${response.code()}")
                Log.e(TAG, "Error body: $errorBody")
                Result.Error(Exception("Помилка при підключенні Монобанку: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в connectMonobank", e)
            Result.Error(e)
        }
    }
}