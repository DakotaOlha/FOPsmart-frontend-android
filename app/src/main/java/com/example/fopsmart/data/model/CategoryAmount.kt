package com.example.fopsmart.data.model

import com.google.gson.annotations.SerializedName

data class CategoryAmount(
    @SerializedName("mcc")
    val mcc: String? = null,

    @SerializedName("category")
    val category: String,

    @SerializedName("totalSpent")
    val totalSpent: Double
)