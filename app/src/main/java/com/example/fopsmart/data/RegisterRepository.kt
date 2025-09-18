package com.example.fopsmart.data

import com.example.fopsmart.data.model.RegisterRequest
import com.example.fopsmart.data.network.RetrofitClient

class RegisterRepository {
    suspend fun register(email: String, password: String) =
        RetrofitClient.api.register(RegisterRequest(email, password))
}
