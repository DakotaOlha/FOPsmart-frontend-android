package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class PeriodInfo(
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String,
    @SerializedName("days")
    val days: Int
)
