package com.example.fopsmart.data.network

import com.example.fopsmart.data.model.ChatRequest
import com.example.fopsmart.data.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiApiService {
    @POST("api/chat")
    suspend fun postChat(@Body request: ChatRequest): Response<ChatResponse>
}