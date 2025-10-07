package com.example.fopsmart.data

import com.example.fopsmart.data.model.LoggedInUser
import com.example.fopsmart.data.model.LoginRequest
import com.example.fopsmart.data.network.RetrofitClient
import java.io.IOException

class LoginRepository {
    private val api = RetrofitClient.api

    suspend fun login(email: String, password: String): Result<LoggedInUser> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null){
                    val loggedInUser = LoggedInUser(
                        userId = body.user.id,
                        displayName = body.user.email
                    )
                    Result.Success(loggedInUser)
                }
                else
                    Result.Error(IOException("Empty response body"))
            } else {
                Result.Error(IOException("Error logging in"))
            }
        } catch (e: Exception) {
            Result.Error(IOException("Network error", e))
        }
    }
}