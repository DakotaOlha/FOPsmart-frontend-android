package com.example.fopsmart.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val MAIN_API_URL = "https://fopsmart-4030403a47a5.herokuapp.com/api/"
    private const val AI_API_URL = "http://10.15.177.14:5000"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // час на підключення
        .readTimeout(30, TimeUnit.SECONDS)     // час на читання даних
        .writeTimeout(30, TimeUnit.SECONDS)    // час на відправку даних
        .retryOnConnectionFailure(true) //повторення запиту при збої
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
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AiApiService::class.java)
    }
}