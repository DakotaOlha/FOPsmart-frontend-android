package com.example.fopsmart.data

import android.util.Log
import com.example.fopsmart.data.model.AccountBalance
import com.example.fopsmart.data.model.AddTransactionRequest
import com.example.fopsmart.data.model.AddTransactionResponse
import com.example.fopsmart.data.model.ConnectRequest
import com.example.fopsmart.data.model.Transaction
import com.example.fopsmart.data.network.RetrofitClient

class TransactionRepository {

    private val apiService = RetrofitClient.mainApi
    private val TAG = "TransactionRepository"

    suspend fun getTransactions(
        token: String,
        dateFrom: String? = null,
        dateTo: String? = null,
        accountId: Int? = null,
        limit: Int = 100,
        type: String = "all"
    ): Result<List<Transaction>> {
        return try {
            Log.d(TAG, "Відправляємо запит getTransactions")
            Log.d(TAG, "Параметри: dateFrom=$dateFrom, dateTo=$dateTo, accountId=$accountId, type=$type")

            val response = apiService.getTransactions(
                token = "Bearer $token",
                dateFrom = dateFrom,
                dateTo = dateTo,
                accountId = accountId,
                limit = limit,
                type = type
            )
            Log.d(TAG, "Response code: ${response.code()}")

            if (response.isSuccessful) {
                try {
                    val body = response.body()
                    Log.d(TAG, "Response body: $body")
                    val transactions = body?.transactions ?: emptyList()
                    Log.d(TAG, "Транзакцій завантажено: ${transactions.size}")
                    Result.Success(transactions)
                } catch (e: Exception) {
                    Log.e(TAG, "Помилка при розборі getTransactions: ${e.message}", e)
                    Result.Error(e)
                }
            } else {
                Log.e(TAG, "Помилка getTransactions: ${response.code()} - ${response.errorBody()?.string()}")
                Result.Error(Exception("Помилка при отриманні транзакцій: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в getTransactions", e)
            Result.Error(e)
        }
    }

    suspend fun checkBankStatus(token: String): Result<Boolean> {
        return try {
            Log.d(TAG, "Відправляємо запит checkBankStatus")
            val response = apiService.getMonoStatus("Bearer $token")
            Log.d(TAG, "Response code: ${response.code()}")

            if (response.isSuccessful) {
                try {
                    val body = response.body()
                    Log.d(TAG, "Response body: $body")
                    val isConnected = body?.connected ?: false
                    Log.d(TAG, "isConnected: $isConnected")
                    Result.Success(isConnected)
                } catch (e: Exception) {
                    Log.e(TAG, "Помилка при розборі checkBankStatus: ${e.message}", e)
                    Result.Error(e)
                }
            } else {
                Log.e(TAG, "Помилка checkBankStatus: ${response.code()} - ${response.errorBody()?.string()}")
                Result.Error(Exception("Помилка при перевірці статусу банку: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в checkBankStatus", e)
            Result.Error(e)
        }
    }

    suspend fun connectMonobank(token: String, monoToken: String): Result<Boolean> {
        return try {
            Log.d(TAG, "Відправляємо запит на підключення Монобанку")
            Log.d(TAG, "User Token: ${token.take(20)}...")
            Log.d(TAG, "Mono Token: ${monoToken.take(20)}...")

            val request = ConnectRequest(token = monoToken)
            Log.d(TAG, "Request body: $request")

            val response = apiService.setMonoConnection("Bearer $token", request)

            Log.d(TAG, "Response code: ${response.code()}")
            Log.d(TAG, "Response headers: ${response.headers()}")

            if (response.code() == 201 || response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Response body: $body")
                Log.d(TAG, "Монобанк успішно підключено")
                Result.Success(true)
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

    suspend fun getAccountBalances(token: String): Result<List<AccountBalance>> {
        return try {
            Log.d(TAG, "Відправляємо запит getAccountBalances")
            val response = apiService.getAccountBalances("Bearer $token")
            Log.d(TAG, "Response code: ${response.code()}")

            if (response.isSuccessful) {
                try {
                    val body = response.body()
                    Log.d(TAG, "Response body: $body")
                    val balances = body?.balances ?: emptyList()
                    Log.d(TAG, "Рахунків завантажено: ${balances.size}")
                    balances.forEach { account ->
                        Log.d(TAG, "Рахунок ID: ${account.id}, Баланс: ${account.balance}, IBAN: ${account.iban}")
                    }
                    Result.Success(balances)
                } catch (e: Exception) {
                    Log.e(TAG, "Помилка при розборі getAccountBalances: ${e.message}", e)
                    Result.Error(e)
                }
            } else {
                Log.e(TAG, "Помилка getAccountBalances: ${response.code()} - ${response.errorBody()?.string()}")
                Result.Error(Exception("Помилка при отриманні балансів: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в getAccountBalances", e)
            Result.Error(e)
        }
    }

    suspend fun addTransaction(
        token: String,
        request: AddTransactionRequest
    ): Result<AddTransactionResponse> {
        return try {
            Log.d(TAG, "Відправляємо запит на додавання транзакції")
            Log.d(TAG, "Request: amount=${request.amount}, type=${request.type}, date=${request.transactionDate}")

            val response = apiService.addTransaction(
                token = "Bearer $token",
                request = request
            )

            Log.d(TAG, "Response code: ${response.code()}")

            if (response.isSuccessful) {
                try {
                    val body = response.body()
                    Log.d(TAG, "Response body: $body")

                    if (body != null) {
                        Log.d(TAG, "Транзакція додана успішно. ID: ${body.transaction.id}")
                        Result.Success(body)
                    } else {
                        Result.Error(Exception("Порожнє тіло відповіді"))
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Помилка при розборі addTransaction: ${e.message}", e)
                    Result.Error(e)
                }
            } else {
                Log.e(TAG, "Помилка addTransaction: ${response.code()} - ${response.errorBody()?.string()}")
                Result.Error(Exception("Помилка при додаванні транзакції: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception в addTransaction", e)
            Result.Error(e)
        }
    }
}