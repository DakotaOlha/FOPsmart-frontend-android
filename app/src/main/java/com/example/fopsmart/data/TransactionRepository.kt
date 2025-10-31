package com.example.fopsmart.data

import com.example.fopsmart.data.model.Transaction
import com.example.fopsmart.data.model.MonobankStatusResponse
import com.example.fopsmart.data.network.RetrofitClient
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
}