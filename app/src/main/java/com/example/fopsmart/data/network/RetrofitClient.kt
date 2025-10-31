package com.example.fopsmart.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val MAIN_API_URL = "https://fopsmart-4030403a47a5.herokuapp.com/api/"
    private const val AI_API_URL = "http://192.168.0.103:5000"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val mainApi: MainApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MAIN_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MainApiService::class.java)
    }

    val aiApi: AiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AI_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AiApiService::class.java)
    }
}