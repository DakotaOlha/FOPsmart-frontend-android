package com.example.fopsmart.data

import android.content.Context
import android.content.SharedPreferences
import com.example.fopsmart.data.model.LoggedInUser
import com.example.fopsmart.data.model.LoginRequest
import com.example.fopsmart.data.network.RetrofitClient
import java.io.IOException

class LoginRepository(private val context: Context? = null) {
    private val api = RetrofitClient.mainApi

    suspend fun login(email: String, password: String): Result<LoggedInUser> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPreferences?.edit()?.apply {
                        putString("auth_token", body.token)
                        apply()
                    }

                    val loggedInUser = LoggedInUser(
                        userId = body.user.id,
                        displayName = body.user.email
                    )
                    Result.Success(loggedInUser)
                } else {
                    Result.Error(IOException("Empty response body"))
                }
            } else {
                Result.Error(IOException("Error logging in: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.Error(IOException("Network error: ${e.message}", e))
        }
    }

    fun logout() {
        val sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.apply {
            remove("auth_token")
            apply()
        }
    }

    fun getStoredToken(): String? {
        val sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences?.getString("auth_token", null)
    }
}