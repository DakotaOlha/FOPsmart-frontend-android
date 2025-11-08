package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class ReportType(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String
)
